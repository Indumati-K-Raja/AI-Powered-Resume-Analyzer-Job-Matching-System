from fastapi import FastAPI, File, UploadFile, HTTPException, BackgroundTasks
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from typing import List, Dict, Optional
import spacy
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import json
import re

app = FastAPI(title="Resume Analyzer NLP Service", version="1.0.0")

# Load spaCy model
try:
    nlp = spacy.load("en_core_web_sm")
except:
    import os
    os.system("python -m spacy download en_core_web_sm")
    nlp = spacy.load("en_core_web_sm")

class SkillExtractionRequest(BaseModel):
    resume_text: str
    skill_taxonomy: Optional[Dict[str, List[str]]] = None

class SkillExtractionResponse(BaseModel):
    detected_skills: List[str]
    skill_confidence: Dict[str, float]
    entities: Dict[str, List[str]]  # PERSON, ORG, PRODUCT, etc.
    certifications: List[str]

# Skill taxonomy mapping (expandable)
SKILL_TAXONOMY = {
    "Programming Languages": ["Python", "Java", "JavaScript", "C++", "Go", "Rust"],
    "Backend": ["Spring Boot", "Django", "FastAPI", "Node.js", "Express", "Kafka"],
    "Frontend": ["React", "Vue", "Angular", "TypeScript", "HTML", "CSS"],
    "Databases": ["MySQL", "PostgreSQL", "MongoDB", "Redis", "Elasticsearch"],
    "DevOps": ["Docker", "Kubernetes", "CI/CD", "GitHub Actions", "AWS", "Azure"],
    "ML/AI": ["TensorFlow", "PyTorch", "Scikit-learn", "spaCy", "NLP", "Computer Vision"],
}

# Synonyms for skills (expand as needed)
SKILL_SYNONYMS = {
    "javascript": ["js", "ecmascript", "node"],
    "kubernetes": ["k8s"],
    "tensorflow": ["tf"],
    "machine learning": ["ml", "deep learning"],
    "natural language processing": ["nlp"],
    "continuous integration": ["ci/cd", "ci", "cd"],
}

@app.post("/extract-skills", response_model=SkillExtractionResponse)
async def extract_skills(request: SkillExtractionRequest):
    """
    Extract skills, entities, and certifications from resume text using NLP.
    """
    try:
        doc = nlp(request.resume_text.lower())
        
        # Extract entities
        entities = {
            "PERSON": [],
            "ORG": [],
            "PRODUCT": [],
            "SKILL": []
        }
        
        for ent in doc.ents:
            if ent.label_ in entities:
                entities[ent.label_].append(ent.text)
        
        # Extract skills using taxonomy matching + NER
        detected_skills = set()
        skill_confidence = {}
        
        # Method 1: Direct keyword matching with synonym resolution
        text_lower = request.resume_text.lower()
        for category, skills in SKILL_TAXONOMY.items():
            for skill in skills:
                skill_lower = skill.lower()
                if skill_lower in text_lower:
                    detected_skills.add(skill)
                    skill_confidence[skill] = 0.95
                else:
                    # Check synonyms
                    for synonym in SKILL_SYNONYMS.get(skill_lower, []):
                        if synonym in text_lower:
                            detected_skills.add(skill)
                            skill_confidence[skill] = 0.85
                            break
        
        # Method 2: NER-based skill detection (for unlisted skills)
        for token in doc:
            if token.ent_type_ in ["PRODUCT", "ORG"] and len(token.text) > 3:
                # Potential tool/framework
                detected_skills.add(token.text)
                skill_confidence[token.text] = 0.70
        
        # Method 3: Pattern-based (e.g., "AWS", "GCP", "CI/CD")
        for pattern in ["aws", "gcp", "azure", "devops", "ci/cd", "ml", "ai", "nlp"]:
            if pattern in text_lower:
                found_in_taxonomy = False
                for category, skills in SKILL_TAXONOMY.items():
                    for skill in skills:
                        if pattern in skill.lower():
                            detected_skills.add(skill)
                            skill_confidence[skill] = 0.80
                            found_in_taxonomy = True
                if not found_in_taxonomy:
                    detected_skills.add(pattern.upper())
                    skill_confidence[pattern.upper()] = 0.75
        
        # Extract certifications (AWS, GCP, Azure, CPA, etc.)
        certifications = []
        cert_patterns = ["aws", "gcp", "azure", "certified", "cpa", "cissp", "pmp"]
        for pattern in cert_patterns:
            if pattern in text_lower:
                certifications.append(pattern.upper())
        
        return SkillExtractionResponse(
            detected_skills=list(detected_skills),
            skill_confidence=skill_confidence,
            entities=entities,
            certifications=certifications
        )
    
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

@app.post("/resume-score")
async def calculate_resume_score(request: SkillExtractionRequest):
    """
    Score resume quality based on:
    - Section completeness (summary, experience, skills, education)
    - Action verbs usage
    - Quantified achievements
    - Skill density
    """
    doc = nlp(request.resume_text)
    text = request.resume_text.lower()
    
    score_components = {
        "section_completeness": 0,
        "action_verbs": 0,
        "quantification": 0,
        "skill_density": 0,
        "overall": 0
    }
    
    # 1. Section completeness (25 points)
    required_sections = ["summary", "experience", "skills", "education"]
    found_sections = sum(1 for section in required_sections if section in text)
    score_components["section_completeness"] = (found_sections / len(required_sections)) * 25
    
    # 2. Action verbs (25 points)
    action_verbs = [
        "led", "developed", "implemented", "designed", "managed", "optimized",
        "increased", "reduced", "improved", "created", "built", "architected",
        "deployed", "automated", "integrated", "analyzed", "achieved"
    ]
    verb_count = sum(1 for verb in action_verbs if verb in text)
    score_components["action_verbs"] = min((verb_count / 5) * 25, 25)  # Max 25
    
    # 3. Quantification (25 points)
    numbers = len(re.findall(r'\d+%|\d+x|\d+\.\d+x|\$\d+[mk]?', text))
    score_components["quantification"] = min((numbers / 5) * 25, 25)
    
    # 4. Skill density (25 points)
    skill_extraction = await extract_skills(request)
    words = len(doc)
    skill_ratio = len(skill_extraction.detected_skills) / max(words / 100, 1)  # skills per 100 words
    score_components["skill_density"] = min(skill_ratio * 10, 25)
    
    # Calculate overall score
    score_components["overall"] = sum([
        score_components["section_completeness"],
        score_components["action_verbs"],
        score_components["quantification"],
        score_components["skill_density"]
    ])
    
    return score_components

@app.post("/match-job-description")
async def match_job_description(request: Dict[str, str]):
    """
    Calculate resume-to-JD match score using TF-IDF cosine similarity.
    Returns: match_score (0-100), missing_skills, matched_skills
    """
    resume_text = request.get("resume_text", "")
    job_description = request.get("job_description", "")
    
    try:
        # Extract skills from both
        resume_skills_resp = await extract_skills(SkillExtractionRequest(resume_text=resume_text))
        jd_skills_resp = await extract_skills(SkillExtractionRequest(resume_text=job_description))
        
        resume_skills = set(resume_skills_resp.detected_skills)
        jd_skills = set(jd_skills_resp.detected_skills)
        
        matched = resume_skills & jd_skills
        missing = jd_skills - resume_skills
        extra = resume_skills - jd_skills
        
        # TF-IDF similarity
        vectorizer = TfidfVectorizer(max_features=100, stop_words='english')
        try:
            vectors = vectorizer.fit_transform([resume_text, job_description])
            similarity = cosine_similarity(vectors)[0][1]
            match_score = similarity * 100
        except:
            match_score = (len(matched) / max(len(jd_skills), 1)) * 100
        
        return {
            "match_score": round(match_score, 2),
            "matched_skills": list(matched),
            "missing_skills": list(missing),
            "extra_skills": list(extra),
            "match_percentage": round((len(matched) / max(len(jd_skills), 1)) * 100, 2) if jd_skills else 0
        }
    
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

@app.post("/improvement-suggestions")
async def get_improvement_suggestions(request: SkillExtractionRequest):
    """
    Provide AI-powered suggestions for resume improvement.
    """
    skill_resp = await extract_skills(request)
    score_resp = await calculate_resume_score(request)
    
    suggestions = []
    
    # Suggestion 1: Low section completeness
    if score_resp["section_completeness"] < 20:
        suggestions.append({
            "category": "Structure",
            "severity": "high",
            "message": "Add missing resume sections: Professional Summary, Work Experience, Skills, Education",
            "impact": "25 points"
        })
    
    # Suggestion 2: Few action verbs
    if score_resp["action_verbs"] < 10:
        suggestions.append({
            "category": "Language",
            "severity": "high",
            "message": "Use strong action verbs: Led, Developed, Implemented, Optimized, Increased instead of passive descriptions",
            "impact": "15 points"
        })
    
    # Suggestion 3: No quantification
    if score_resp["quantification"] < 10:
        suggestions.append({
            "category": "Impact",
            "severity": "medium",
            "message": "Quantify your achievements: '30% improvement', '$2M revenue', '50% faster' to show business impact",
            "impact": "15 points"
        })
    
    # Suggestion 4: Low skill density
    if score_resp["skill_density"] < 10:
        suggestions.append({
            "category": "Skills",
            "severity": "medium",
            "message": f"You have {len(skill_resp.detected_skills)} skills detected. Add more relevant technical skills to your experience bullets.",
            "impact": "10 points"
        })
    
    # Suggestion 5: Missing certifications
    if not skill_resp.certifications:
        suggestions.append({
            "category": "Credentials",
            "severity": "low",
            "message": "Consider adding relevant certifications: AWS, GCP, Kubernetes, Project Management",
            "impact": "5 points"
        })
    
    return {
        "suggestions": suggestions,
        "estimated_improvement": sum([int(s["impact"].split()[0]) for s in suggestions])
    }

@app.get("/health")
async def health_check():
    return {"status": "ok", "model": "spaCy en_core_web_sm"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8001)

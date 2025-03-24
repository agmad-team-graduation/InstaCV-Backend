import json

import numpy as np
from fastapi import FastAPI
from pydantic import BaseModel
from transformers import pipeline

# Load models and tokenizers
knowledge_nlp = pipeline(model="jjzha/jobbert_knowledge_extraction", aggregation_strategy="first")
skill_nlp = pipeline(model="jjzha/jobbert_skill_extraction", aggregation_strategy="first")

app = FastAPI()


class TextInput(BaseModel):
    jobDescription: str


def convert_from_numpy(predictions):
    for pred in predictions:
        for key, value in pred.items():
            if isinstance(value, (np.float32, np.int32, np.int64)):  # Handle NumPy numeric types
                pred[key] = float(value)
    return predictions


def merge_BI_and_get_results(predictions):
    results, curSkill, curScore, curNoWords = [], "", 0, 0
    for pred in predictions:
        if pred['entity_group'] == 'B':
            if curSkill:
                results.append({"name": curSkill, "confidence": curScore / curNoWords})  # Average confidence
            curSkill, curScore, curNoWords = pred['word'], pred['score'], 1
        else:
            curSkill += " " + pred['word']
            curScore += pred['score']
            curNoWords += 1
    if curSkill:
        results.append({"name": curSkill, "confidence": curScore / curNoWords})
    return results


@app.post("/predict_knowledge")
def predict_knowledge(input_data: TextInput):
    predictions = knowledge_nlp(input_data.jobDescription)
    predictions = convert_from_numpy(predictions)
    # print(json.dumps(predictions, indent=2))
    return {"knowledge_predictions": merge_BI_and_get_results(predictions)}


@app.post("/predict_skills")
def predict_skills(input_data: TextInput):
    predictions = skill_nlp(input_data.jobDescription)
    predictions = convert_from_numpy(predictions)
    # print(json.dumps(predictions, indent=2))
    return {"skills_predictions": merge_BI_and_get_results(predictions)}

# Run with:
# uvicorn main:app --host 0.0.0.0 --port 8000

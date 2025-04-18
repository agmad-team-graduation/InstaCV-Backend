from pydantic import BaseModel
from typing import List


class BaseSkill(BaseModel):
    id: int
    skill: str


class UserSkill(BaseSkill):
    pass


class ProjectSkill(BaseSkill):
    pass


class JobSkill(BaseSkill):
    pass


class Project(BaseModel):
    id: int
    skills: List[ProjectSkill]

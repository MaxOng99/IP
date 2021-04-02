from typing import Mapping, List
from pydantic import BaseModel

class EvaluationResult(BaseModel):
	response: Mapping[int, int] 
	far: float
	frr: float
	eer: float
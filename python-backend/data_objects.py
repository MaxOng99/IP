from typing import Mapping, List
from pydantic import BaseModel

class TouchGestureData(BaseModel):
	data: List[Mapping[str, float]]
	date: str
	sample_id: int

class UserTGDMapping(BaseModel):
	experiment_type: str
	user_tgd_map: Mapping[int, List[TouchGestureData]]

class EvaluationResult(BaseModel):
	response: Mapping[int, int] 
	far: float
	frr: float
	eer: float


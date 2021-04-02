"""
Data classes that encapsulates the structure of the request body
required by the 'predict' endpoint of the API.
"""

from typing import Mapping, List
from pydantic import BaseModel


class TouchGestureData(BaseModel):
	"""
	Encapsulates a single sample of participant's Touch Gesture Data.
	- data: 4-Element list of raw touch gesture data (Refer to repo on raw data structure)
	- date: Date the sample was collected.
	- sample_id: Identifier for this sample.
	"""
	data: List[Mapping[str, float]]
	date: str
	sample_id: int


class UserTGDMapping(BaseModel):
	"""
	- Request body required by the 'predict' API endpoint.
	- experiment_type: Either 'keystroke' or 'reaction' - dictates the type of model used.
	- user_tgd_map: Mapping of user ids to their respective TouchGestureData.
	"""
	experiment_type: str
	user_tgd_map: Mapping[int, List[TouchGestureData]]

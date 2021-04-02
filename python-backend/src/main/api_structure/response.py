"""
Data class that encapsulates the structure of the response body
for the 'predict' API endpoint.
"""

from typing import Mapping
from pydantic import BaseModel

class EvaluationResult(BaseModel):
	"""
	- response: Mapping of user ids to their number of accepted authentications.
	- far: False Error Rate for this test instance.
	- frr: False Rejection Rate for this test instance.
	- eer: False Error Rate for this test instance.
	"""
	response: Mapping[int, int]
	far: float
	frr: float
	eer: float
	
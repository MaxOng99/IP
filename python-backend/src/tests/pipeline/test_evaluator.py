import pytest
from main.pipeline.evaluator import Evaluator

# generate test cases
def generate_scores():
	samples = []

	# Auth system at 100% sensitivity
	true_1 = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1]
	pred_1 = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]
	far_1 = 0
	frr_1 = 1

	sample_1 = (true_1, pred_1, far_1, frr_1)
	samples.append(sample_1)

	# Auth system at 0% sensitivity
	true_2 = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]
	pred_2 = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1]
	far_2 = 1
	frr_2 = 0

	sample_2 = (true_2, pred_2, far_2, frr_2)
	samples.append(sample_2)

	# Auth system that performs opposite predictions (worst classifier)
	true_3 = [1, -1, 1, -1, 1, -1, 1, -1, 1, -1]
	pred_3 = [-1, 1, -1, 1, -1, 1, -1, 1, -1, 1]
	far_3 = 5/5
	frr_3 = 5/5

	# Auth system that is equivalent to guessing 
	true_4 = [1, 1, 1, 1, 1, -1, -1, -1, -1, -1]
	pred_4 = [1, -1, 1, -1, 1, -1, 1, -1, 1, -1]
	far_4 = 2/5
	frr_4 = 2/5

	sample_4 = (true_4, pred_4, far_4, frr_4)
	samples.append(sample_4)

	return samples

# unit tests
@pytest.fixture
def dummy_evaluator():
	return Evaluator("test_model", "test_dataframes", "test_uid")

@pytest.fixture(params=generate_scores())
def sample_generator(request):
	return request.param

def test_calculate_far(dummy_evaluator, sample_generator):
	true, pred, far, frr = sample_generator
	assert dummy_evaluator.calculate_far(true, pred) == far 

def test_calculate_frr(dummy_evaluator, sample_generator):
	true, pred, far, frr = sample_generator
	assert dummy_evaluator.calculate_frr(true, pred) == frr 




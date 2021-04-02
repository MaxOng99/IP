cd python-backed/

pip install -r scripts/requirements.txt -t /workspace/python-backend/lib
pip install -e -t /python-backend/lib

cd src/tests/
python -m pytest


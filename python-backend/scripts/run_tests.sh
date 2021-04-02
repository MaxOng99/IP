pip install -r python-backend/scripts/requirements.txt -t /workspace/python-backend/lib
pip install -e src/main -t /workspace/python-backend/lib

cd python-backend/src/tests/
python -m pytest
cd ../../../

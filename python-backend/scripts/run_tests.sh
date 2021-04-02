cd python-backed/
mkdir lib

pip install -r scripts/requirements.txt -t /lib
pip install -e -t /lib

cd src/tests/
python -m pytest


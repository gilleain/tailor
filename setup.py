import os
from distutils.core import setup

name = "Tailor"
author = "Gilleain Torrance"
author_email = "gilleain.torrance@gmail.com"
url = ""
description = "Measure geometric features of arbitrary features of proteins."
license = "None"
version = "1.2"

data_files = [(os.path.join(os.getcwd(), "examples"),
                ['examples/nestmeasure.py', 'examples/simple.py'])]

setup(
        name = name,
        author = author,
        author_email = author_email,
        url = url,
        description = description,
        license = license,
        version = version,
        packages = ["Tailor"],
        data_files = data_files
    ) 

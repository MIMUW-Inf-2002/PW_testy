import os
import subprocess
import sys

ONE_TEST_FILE = None
if len(sys.argv) != 2 and len(sys.argv) != 4:
    print("Use cases:")
    print("python3 test.py <path/to/src>")
    print("python3 test.py <path/to/src> <path/to/test/file> N_REPEAT")
    exit()
elif len(sys.argv) == 4:
    ONE_TEST_FILE = os.path.abspath(sys.argv[2])
    REPEAT = int(sys.argv[3])
    

PATH_TO_CMAKE = os.path.abspath(sys.argv[1])
# FLAGS = '-Wall -Wextra -Werror -O2'.split(' ')
BUILD_DIR_NAME = 'build_tests'
TEST_FOLDER_NAME = 'tests'
VALGRIND = False

# Returns boolean value for tested output.
# Sections in '======\n' brackets can be in different order. ('\n' is important here)
def files_match(tested_file, schema_file):
    # Open the first file for reading
    with open(tested_file, "r") as f1:
        # Read the lines of the first file
        tested_lines = f1.readlines()

    # Open the second file for reading
    try:
        with open(schema_file, "r") as f2:
            # Read the lines of the second file
            schema_lines = f2.readlines()
    except FileNotFoundError:
        print(f"Error: The file {schema_file} does not exist.")
        print(f"But program output is still wrote to {tested_file}")
        return False

    # Remove pid number from every line
    for i, (tested_line, schema_line) in enumerate(zip(tested_lines, schema_lines)):
        if ' pid ' in tested_line:
            tested_lines[i] = tested_line.split(' pid ')[0]
        if ' pid ' in schema_line:
            schema_lines[i] = schema_line.split(' pid ')[0]

    schema_section_start = None
    schema_section_end = None
    tested_section_start = None
    tested_section_end = None
    tested_i = 0
    schema_i = 0

    # Iterate over the lines in the two files
    while schema_i < len(schema_lines):
        if schema_lines[schema_i] == '======\n':
            if schema_section_start is None:
                schema_section_start = schema_i + 1
                tested_section_start = tested_i

            else:
                schema_section_end = schema_i - 1
                tested_section_end = tested_i - 1
                if (set(schema_lines[schema_section_start: schema_section_end + 1]) 
                    != set(tested_lines[tested_section_start: tested_section_end + 1])):
                    print(f"Lines from {tested_section_start + 1} to {tested_section_end + 1} wrong.")
                    return False
                
                schema_section_start = None
                schema_section_end = None
                
            schema_i += 1
        else:
            if (tested_i >= len(tested_lines)):
                print("Expected more lines in output")
                return False
                
            if schema_lines[schema_i] != tested_lines[tested_i] and schema_section_start is None:    
                print(f"line {tested_i + 1}:\n\t'{tested_lines[tested_i][:-1]}'\n\tbut expected: \n\t'{schema_lines[schema_i][:-1]}'")
                return False
            # else:
            #     print(f"line {tested_i}:\n\t'{tested_lines[tested_i][:-1]}'\n\tis the same as: \n\t'{schema_lines[schema_i][:-1]}'")

            schema_i += 1
            tested_i += 1
    
    if (tested_i < len(tested_lines)):
        print("Expected less lines in output")
        return False
    # If all lines are the same, return True
    return True

# Compiles all .c programs, given the name of test folder.
# Returns filenems of the executables compiled.
def build_for_dir(dir_name):
    os.chdir(f"{TEST_FOLDER_NAME}/{dir_name}")
    # Find all the .c files in the directory
    c_files = [f for f in os.listdir() if f.endswith(".c")]
    os.chdir("../..")

    # Compile each .c file into an executable
    for c_file in c_files:
        exe_name = c_file[:-2]  # remove the .c extension
        subprocess.run(["gcc", f"{TEST_FOLDER_NAME}/{dir_name}/{c_file}", "-o", f"{BUILD_DIR_NAME}/{exe_name}"])

    return [c_file[:-2] for c_file in c_files]

# Test file given the name of the test file directory and name of the test input file.
def test_file(dir_name, test_in_file):
    input_file_path = os.path.abspath(f"{TEST_FOLDER_NAME}/{dir_name}/{test_in_file}")
    output_file_path = os.path.abspath(f"{BUILD_DIR_NAME}/{test_in_file[:-3]}.out")
    print("Testing file:", f"{TEST_FOLDER_NAME}/{dir_name}/{test_in_file}")

    os.chdir(BUILD_DIR_NAME)
    # Execute the program with input and output redirection
    if VALGRIND:
        result = subprocess.run(['valgrind', '--error-exitcode=123',  '--tool=helgrind', '-q', './executor',], 
                        stdin=open(input_file_path), 
                        stdout=subprocess.PIPE
                        )
        os.chdir("..")
        
        if result.returncode == 0:
            print("\033[32mHELGRIND OK\033[0m")
        else:
            print("\033[31mHELGRIND FAILED\033[0m")
        
        return

    else:
        result = subprocess.run(['./executor'], 
                        stdin=open(input_file_path), 
                        stdout=open(output_file_path, "w"))
        os.chdir("..")
        if (result.returncode != 0):
            print(f"Process return code = {result.returncode}")
            print("\033[31mTEST FAILED\033[0m")
            return

    if files_match(output_file_path, f"{TEST_FOLDER_NAME}/{dir_name}/{test_in_file[:-3]}.out"):
        print("\033[32mTEST PASSED\033[0m")
    else:
        print("\033[31mTEST FAILED\033[0m")

def test_for_dir(dir_name):
    build_for_dir(dir_name)

    # Get the list of files in the subdirectory
    files = os.listdir(f"{TEST_FOLDER_NAME}/{dir_name}")
    # Filter the list to only include files ending with ".in"
    in_files = [f for f in files if f.endswith(".in")]
    
    for test_in_file in sorted(in_files):
        test_file(dir_name, test_in_file)
    # remove some files?


# ============== START OF THE PROGRAM ================
try:
    os.mkdir(BUILD_DIR_NAME)
except FileExistsError:
    print("Directory already exists")

os.chdir(BUILD_DIR_NAME)
subprocess.run(["cmake", PATH_TO_CMAKE])
subprocess.run(["make"])
os.chdir(f"..")

if ONE_TEST_FILE:
    test_dir_name = os.path.dirname(ONE_TEST_FILE)
    test_dir_name = os.path.basename(test_dir_name)
    print(test_dir_name)
    build_for_dir(test_dir_name)

    for i in range(REPEAT):
        test_file(test_dir_name, os.path.basename(ONE_TEST_FILE))
    
    VALGRIND = True
    test_file(test_dir_name, os.path.basename(ONE_TEST_FILE))
    
else:
    print()
    print("=============== OUTPUT BASED TESTS ===============")
    print(f"Program outputs are saved to '{BUILD_DIR_NAME}'.")
    print()

    # Iterate over all the directories in the test directory
    for entry in sorted(os.scandir(TEST_FOLDER_NAME), key=lambda e: e.name):
        if entry.is_dir():
            test_for_dir(entry.name)
    print()
    print("=============== VALGRIND TESTS ===============")
    print("They do not compare outputs!")
    print()

    VALGRIND = True
    for entry in sorted(os.scandir(TEST_FOLDER_NAME), key=lambda e: e.name):
        if entry.is_dir():
            test_for_dir(entry.name)
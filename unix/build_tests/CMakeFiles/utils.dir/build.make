# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.22

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:

#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:

# Disable VCS-based implicit rules.
% : %,v

# Disable VCS-based implicit rules.
% : RCS/%

# Disable VCS-based implicit rules.
% : RCS/%,v

# Disable VCS-based implicit rules.
% : SCCS/s.%

# Disable VCS-based implicit rules.
% : s.%

.SUFFIXES: .hpux_make_needs_suffix_list

# Command-line flag to silence nested $(MAKE).
$(VERBOSE)MAKESILENT = -s

#Suppress display of executed commands.
$(VERBOSE).SILENT:

# A target that is always out of date.
cmake_force:
.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/bin/cmake

# The command to remove a file.
RM = /usr/bin/cmake -E rm -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/wojtek/mim-projekty/pw/posix

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/wojtek/mim-projekty/pw/PW_testy/unix/build_tests

# Include any dependencies generated for this target.
include CMakeFiles/utils.dir/depend.make
# Include any dependencies generated by the compiler for this target.
include CMakeFiles/utils.dir/compiler_depend.make

# Include the progress variables for this target.
include CMakeFiles/utils.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/utils.dir/flags.make

CMakeFiles/utils.dir/utils.c.o: CMakeFiles/utils.dir/flags.make
CMakeFiles/utils.dir/utils.c.o: /home/wojtek/mim-projekty/pw/posix/utils.c
CMakeFiles/utils.dir/utils.c.o: CMakeFiles/utils.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/wojtek/mim-projekty/pw/PW_testy/unix/build_tests/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building C object CMakeFiles/utils.dir/utils.c.o"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -MD -MT CMakeFiles/utils.dir/utils.c.o -MF CMakeFiles/utils.dir/utils.c.o.d -o CMakeFiles/utils.dir/utils.c.o -c /home/wojtek/mim-projekty/pw/posix/utils.c

CMakeFiles/utils.dir/utils.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/utils.dir/utils.c.i"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /home/wojtek/mim-projekty/pw/posix/utils.c > CMakeFiles/utils.dir/utils.c.i

CMakeFiles/utils.dir/utils.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/utils.dir/utils.c.s"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /home/wojtek/mim-projekty/pw/posix/utils.c -o CMakeFiles/utils.dir/utils.c.s

# Object files for target utils
utils_OBJECTS = \
"CMakeFiles/utils.dir/utils.c.o"

# External object files for target utils
utils_EXTERNAL_OBJECTS =

libutils.a: CMakeFiles/utils.dir/utils.c.o
libutils.a: CMakeFiles/utils.dir/build.make
libutils.a: CMakeFiles/utils.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/wojtek/mim-projekty/pw/PW_testy/unix/build_tests/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Linking C static library libutils.a"
	$(CMAKE_COMMAND) -P CMakeFiles/utils.dir/cmake_clean_target.cmake
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/utils.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/utils.dir/build: libutils.a
.PHONY : CMakeFiles/utils.dir/build

CMakeFiles/utils.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/utils.dir/cmake_clean.cmake
.PHONY : CMakeFiles/utils.dir/clean

CMakeFiles/utils.dir/depend:
	cd /home/wojtek/mim-projekty/pw/PW_testy/unix/build_tests && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/wojtek/mim-projekty/pw/posix /home/wojtek/mim-projekty/pw/posix /home/wojtek/mim-projekty/pw/PW_testy/unix/build_tests /home/wojtek/mim-projekty/pw/PW_testy/unix/build_tests /home/wojtek/mim-projekty/pw/PW_testy/unix/build_tests/CMakeFiles/utils.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/utils.dir/depend


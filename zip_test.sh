# Czy jest jeden argument.


echo "Created files are going to be removed. Do not debug on them!"

if [ "$#" -ne 1 ]; then
 echo "$Usage ./zip_test ab123456.zip$";
 exit 1;
fi;

# Czy argument jest postaci ab123456.zip?


if [[ $1 != [a-z][a-z][0-9][0-9][0-9][0-9][0-9][0-9].zip ]]; then
	echo "$Usage ./zip_test ab123456.zip$";
fi;

# Folder temp_test/cp2022 zawierać będzie rozwiązanie, testy i czyste pliki dołączone w szablonie.

if [ -d temp_test ]; then
	rm -rf temp_test;
fi;



mkdir temp_test 

if unzip $1 -d ./temp_test > /dev/null; then 
	echo " Unzip succedeed."; else
	echo "Unzip error."
	exit 1;
fi;

cd temp_test

if [ ! -d "cp2022" ]; then
	echo "$Error. Zip archive should contain cp-2022 directory.";
	exit 1;
fi;

echo -e "Removing unnecesary files."

shopt -s extglob # Jakaś stała, żeby następna komenda działała
rm -rf -- !('cp2022') # Usuwamy foldery inne niż cp2022

cd cp2022
rm -rf -- !('solution') # Usuwamy foldery inne niż soluton

cd solution
rm -rf -- !(*.java) # Usuwamy pliki inne niż *.java

echo "Copying the demo, base and the tests."

cd ..
cd ..
cd ..

cp -r ./template/base temp_test/cp2022
cp -r ./template/demo temp_test/cp2022
cp -r ./tests temp_test/cp2022

echo "$Compilation"

cd temp_test
find  . -name "*.java" > test_sources.txt
javac cp2022/base/*.java cp2022/solution/*.java cp2022/demo/*.java @test_sources.txt
tm test_sources.txt

echo "Running demo app."

java cp2022.demo.TroysWorkshop

echo "Running tests."

java cp2022.tests.Main



rm -rf temp_test

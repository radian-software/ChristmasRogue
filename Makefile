all:
	rm -rf jar **/*.class
	mkdir jar
	cd src && javac com/apprisingsoftware/xmasrogue/ChristmasRogue.java
	cd src && jar cvef com.apprisingsoftware.xmasrogue.ChristmasRogue ../jar/ChristmasRogue.jar $$(find . -name '*.class' -o -name '*.png')

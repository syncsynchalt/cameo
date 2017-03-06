all:
	find . -name '*.java' -exec javac {} \;

clean:
	find . -name '*.class' -delete

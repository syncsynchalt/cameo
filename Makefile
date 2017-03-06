SOURCES=$(shell find . -name '*.java')
CLASSES=$(patsubst %.java, %.class, $(SOURCES))

all: $(CLASSES)

%.class: %.java
	javac $^

clean:
	find . -name '*.class' -delete

run: simulator.jar antlr-3.4-complete.jar bin/arch/y86/machine/seq/student/CPU.class
	java -cp antlr-3.4-complete.jar:simulator.jar:bin SimpleMachine -i gui -a y86-seq -v student
bin/arch/y86/machine/seq/student/CPU.class: src/arch/y86/machine/seq/student/CPU.java
	javac -d bin -cp antlr-3.4-complete.jar:simulator.jar:src:bin src/arch/y86/machine/seq/student/CPU.java

simulator.jar:
	$(error 'Copy the antlr-3.4-complete.jar and simulator.jar files to this directory')
antlr-3.4-complete.jar:
	$(error 'Copy the antlr-3.4-complete.jar and simulator.jar files to this directory')



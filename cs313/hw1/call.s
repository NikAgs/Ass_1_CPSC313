.pos 0x100
	xorq %rdx, %rdx
	irmovq $b, %rbx
	call *0x1(%rbx), %rax
	iaddq 0x10, %rdx
	halt

.pos 0x1000
	pushq %rax
	iaddq 0x2, %rdx
	ret	

.pos 0x1100
	pushq %rax
	iaddq 0x3, %rdx
	ret

.pos 0x2000
b:		.quad 0x00001000


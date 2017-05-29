.pos 0x100
	xorq %rdx, %rdx
	irmovq 0x1000, %rbx
	call *0x100(%rbx), %rax
	# call *0x1000(%rbx), %rax
	iaddq 0x10, %rdx
	halt

.pos 0x1100
	pushq %rax
	iaddq 0x3, %rdx
	ret


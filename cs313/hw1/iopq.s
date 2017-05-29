main: 
	iaddq 10, %rax                  # %rax = 10
	isubq 5, %rax	                 # %rax = 5
	imulq 3, %rax                   # %rax = 15
	idivq 5, %rax                   # %rax = 3
	imodq 2, %rax                   # %rax = 1
	iaddq 0xfffffffffffffffe, %rax  # %rax = 0xffffffffffffffff
	ixorq 0, %rax                   # %rax = 0xffffffffffffffff
	iandq 0, %rax                   # %rax = 0
	isubq 20, %rax                  # %rax = -20
	idivq 7, %rax                   # %rax = 2.85…
	
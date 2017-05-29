	.file	"heapsort.c"
	.text
	.p2align 4,,15
	.globl	heapsort
	.type	heapsort, @function
heapsort:
.LFB11:
	.cfi_startproc
	pushq	%rbx                  # push old value of %rbx into stack (callee-saved)
	.cfi_def_cfa_offset 16
	.cfi_offset 3, -16
	movl	%edi, %ebx            # save last in %ebx (%ebx instead of %rbx because last is int)
	call	heapify_array         # call heapify_array with changed %rbx
	testl	%ebx, %ebx            # set condition codes comparing %ebx
	js	.L1                       # goto .L1 if last < 0
	.p2align 4,,10
	.p2align 3
.L7:
	movl	%ebx, %edi            # save i in %edi
	call	extract_max           # call extract_max with changed %edi
	movslq	%ebx, %rdx            # save sign-extended i in $rdx
	subl	$1, %ebx              # i gets subtracted by 1
	cmpl	$-1, %ebx             # compare i and -1 and set condition codes
	movl	%eax, heap(,%rdx,4)   # save %eax (set by extract_max) to heap[i]
	jne	.L7                       # goto .L7 if i >= 0
.L1:
	popq	%rbx                  # restore old value of %rbx
	.cfi_def_cfa_offset 8
	ret                           # done, return
	.cfi_endproc
.LFE11:
	.size	heapsort, .-heapsort
	.ident	"GCC: (SUSE Linux) 4.8.5"
	.section	.note.GNU-stack,"",@progbits

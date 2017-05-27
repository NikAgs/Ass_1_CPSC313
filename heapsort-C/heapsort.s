	.section	__TEXT,__text,regular,pure_instructions
	.macosx_version_min 10, 11
	.globl	_heapsort
	.align	4, 0x90
_heapsort:                              ## @heapsort
	.cfi_startproc
## BB#0:
	pushq	%rbp
Ltmp0:
	.cfi_def_cfa_offset 16
Ltmp1:
	.cfi_offset %rbp, -16
	movq	%rsp, %rbp
Ltmp2:
	.cfi_def_cfa_register %rbp
	pushq	%r15
	pushq	%r14
	pushq	%rbx
	pushq	%rax
Ltmp3:
	.cfi_offset %rbx, -40
Ltmp4:
	.cfi_offset %r14, -32
Ltmp5:
	.cfi_offset %r15, -24
	movl	%edi, %r14d
	callq	_heapify_array
	testl	%r14d, %r14d
	js	LBB0_3
## BB#1:                                ## %.lr.ph.preheader
	movslq	%r14d, %rbx
	incq	%rbx
	movq	_heap@GOTPCREL(%rip), %r15
	.align	4, 0x90
LBB0_2:                                 ## %.lr.ph
                                        ## =>This Inner Loop Header: Depth=1
	movl	%r14d, %edi
	callq	_extract_max
	movl	%eax, -4(%r15,%rbx,4)
	decl	%r14d
	decq	%rbx
	jg	LBB0_2
LBB0_3:                                 ## %._crit_edge
	addq	$8, %rsp
	popq	%rbx
	popq	%r14
	popq	%r15
	popq	%rbp
	retq
	.cfi_endproc


.subsections_via_symbols

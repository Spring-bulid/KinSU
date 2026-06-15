# Rekernel Kernel Driver
# Author: spring bulid
#
# Usage:
#   export ARCH=arm64
#   export CROSS_COMPILE=aarch64-linux-android-
#   make -C /path/to/kernel/source M=$(pwd) modules

MODULE_NAME := rekernel

ifneq ($(KERNELRELEASE),)
	include Kbuild
else
	KDIR ?= /lib/modules/$(shell uname -r)/build
	PWD := $(shell pwd)

default:
	$(MAKE) -C $(KDIR) M=$(PWD) modules

clean:
	$(MAKE) -C $(KDIR) M=$(PWD) clean

modules_install:
	$(MAKE) -C $(KDIR) M=$(PWD) modules_install
endif
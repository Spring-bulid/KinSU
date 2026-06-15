#!/bin/bash
set -u
# Build android15-6.6 and android16-6.12 with BTF disabled (pahole too old)
declare -A CLANG_MAP=(
  ["android15-6.6"]="clang-r510928"
  ["android16-6.12"]="clang-r536225"
)

KERN=/root/rekernel-kernel
OUT=/mnt/d/rekernel/_ko_output
mkdir -p "$OUT"

cd "$KERN"
for kmi in android15-6.6 android16-6.12; do
  clang=${CLANG_MAP[$kmi]}
  KDIR=/opt/ddk/kdir/$kmi
  echo ""
  echo "============================================"
  echo "BUILDING $kmi with $clang (BTF disabled)"
  echo "============================================"

  make -C "$KDIR" M="$KERN" src="$KERN" clean 2>/dev/null 1>&2 || true
  rm -f "$KERN"/kernelsu.ko "$KERN"/kernelsu.o "$KERN"/Module.symvers 2>/dev/null

  export KDIR
  export PATH=/opt/ddk/clang/$clang/bin:$PATH
  export CROSS_COMPILE=aarch64-linux-gnu-
  export ARCH=arm64
  export LLVM=1
  export LLVM_IAS=1
  export CONFIG_KSU=m

  if make PAHOLE=/bin/true RESOLVE_BTFIDS=/bin/true -C "$KDIR" M="$KERN" src="$KERN" modules -j"$(nproc)" 2>&1 | tail -3; then
    if [ -f "$KERN/kernelsu.ko" ]; then
      llvm-strip -d "$KERN/kernelsu.ko"
      cp "$KERN/kernelsu.ko" "$OUT/${kmi}_rekernel.ko"
      sz=$(stat -c%s "$OUT/${kmi}_rekernel.ko")
      echo "OK $kmi -> ${kmi}_rekernel.ko ($sz bytes)"
    else
      echo "FAIL $kmi: no kernelsu.ko produced"
    fi
  else
    echo "FAIL $kmi: compile error"
  fi
done

echo ""
echo "ALL OUTPUT FILES:"
ls -la "$OUT"/*.ko 2>/dev/null
echo "DONE_REMAINING"

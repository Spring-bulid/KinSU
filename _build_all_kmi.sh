#!/bin/bash
set -u

# === IMPORTANT: Before building, update KSU_EXPECTED_SIZE and KSU_EXPECTED_HASH ===
# Run: python3 /mnt/d/rekernel/extract_cert.py <path-to-rekernel-manager.apk>
# Then update the values in /mnt/d/rekernel/kernel/Kbuild:
#   KSU_EXPECTED_SIZE := 0x<size_from_output>
#   KSU_EXPECTED_HASH := <hash_from_output>
#
# The default values in Kbuild are placeholder/dummy signatures:
#   KSU_EXPECTED_SIZE := 0x2e8
#   KSU_EXPECTED_HASH := 48c973bf9702ba7013e5986e45996454e0bc8389397737b75dfe12903deb1ad9
# These MUST be updated to match the actual Rekernel Manager APK before production builds.
#
# Build rekernel.ko for all 7 KMIs using ddk host mode
# KMI -> clang mapping (from ddk mapping.json)
declare -A CLANG_MAP=(
  ["android12-5.10"]="clang-r416183b"
  ["android13-5.10"]="clang-r450784e"
  ["android13-5.15"]="clang-r450784e"
  ["android14-5.15"]="clang-r487747c"
  ["android14-6.1"]="clang-r487747c"
  ["android15-6.6"]="clang-r510928"
  ["android16-6.12"]="clang-r536225"
)

KERN=/root/rekernel-kernel
OUT=/mnt/d/rekernel/_ko_output
mkdir -p "$OUT"

cd "$KERN"
for kmi in android12-5.10 android13-5.10 android13-5.15 android14-5.15 android14-6.1 android15-6.6 android16-6.12; do
  clang=${CLANG_MAP[$kmi]}
  KDIR=/opt/ddk/kdir/$kmi
  echo ""
  echo "============================================"
  echo "BUILDING $kmi with $clang"
  echo "============================================"

  # clean previous build artifacts
  make -C "$KDIR" M="$KERN" src="$KERN" clean 2>/dev/null 1>&2 || true
  rm -f "$KERN"/kernelsu.ko "$KERN"/kernelsu.o "$KERN"/Module.symvers 2>/dev/null

  export KDIR
  export PATH=/opt/ddk/clang/$clang/bin:$PATH
  export CROSS_COMPILE=aarch64-linux-gnu-
  export ARCH=arm64
  export LLVM=1
  export LLVM_IAS=1
  export CONFIG_KSU=m

  if make -C "$KDIR" M="$KERN" src="$KERN" modules -j"$(nproc)" 2>&1 | tail -3; then
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
echo "============================================"
echo "FINAL OUTPUT in $OUT:"
echo "============================================"
ls -la "$OUT"/*.ko 2>/dev/null
echo ""
echo "DONE"

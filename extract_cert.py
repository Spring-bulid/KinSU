#!/usr/bin/env python3
"""Extract v2 signature certificate (size + sha256) exactly as KernelSU kernel module does.

Replicates check_block() / check_v2_signature() from kernel/manager/apk_sign.c:
  1. Find End of Central Directory (EOCD)
  2. Find APK Signing Block (magic 'APK Sig Block 42')
  3. Find v2 signing block (id 0x7109871a)
  4. Parse: signer-sequence-len, signer-len, signed-data-len, digests-seq-len(+skip),
            certs-len, cert-len  -> read cert bytes
  5. SHA256 of cert bytes, and cert length
"""
import struct
import hashlib
import sys

def find_eocd(data):
    # EOCD signature 0x06054b50, search from end
    for i in range(0xffff + 1):
        off = len(data) - 22 - i
        if off < 0:
            break
        if data[off:off+4] == b'\x50\x4b\x05\x06':
            return off
    raise RuntimeError("EOCD not found")

def main(apk_path):
    with open(apk_path, 'rb') as f:
        data = f.read()

    eocd = find_eocd(data)
    # EOCD: sig(4) disk(2) cd_disk(2) recs_disk(2) recs_total(2) cd_size(4) cd_off(4) comment_len(2)
    cd_off = struct.unpack('<I', data[eocd+16:eocd+20])[0]
    # KernelSU: pos = cd_off - 0x18
    pos = cd_off - 0x18
    size8 = struct.unpack('<Q', data[pos:pos+8])[0]
    magic = data[pos+8:pos+8+16]
    if magic[:16] != b'APK Sig Block 42':
        raise RuntimeError(f"bad magic: {magic}")
    # KernelSU: pos = cd_off - (size8 + 0x8)
    pos = cd_off - (size8 + 8)
    size_of_block = struct.unpack('<Q', data[pos:pos+8])[0]
    if size_of_block != size8:
        raise RuntimeError("block size mismatch")

    # iterate pairs: len(8) + id(4) + value...
    cur = pos + 8
    end = pos + 8 + size8 - 8  # approximate; loop until size8 == size_of_block
    found_v2 = False
    v2_count = 0
    cert_size = None
    cert_sha = None
    while cur < len(data) - 8:
        seq_len = struct.unpack('<Q', data[cur:cur+8])[0]
        if seq_len == size_of_block:
            break
        bid = struct.unpack('<I', data[cur+8:cur+12])[0]
        value_start = cur + 12  # after len(8)+id(4); offset=4
        if bid == 0x7109871a:
            v2_count += 1
            # parse like check_block: p starts at value_start
            p = value_start
            # signer-sequence length
            signer_seq_len, = struct.unpack('<I', data[p:p+4]); p += 4
            # signer length
            signer_len, = struct.unpack('<I', data[p:p+4]); p += 4
            # signed data length
            signed_data_len, = struct.unpack('<I', data[p:p+4]); p += 4
            # digests-sequence length
            digests_len, = struct.unpack('<I', data[p:p+4]); p += 4
            p += digests_len  # skip digests
            # certificates length
            certs_len, = struct.unpack('<I', data[p:p+4]); p += 4
            # first certificate length
            cert_len, = struct.unpack('<I', data[p:p+4]); p += 4
            cert_bytes = data[p:p+cert_len]
            cert_sha256 = hashlib.sha256(cert_bytes).hexdigest()
            print(f"v2 block #{v2_count}:")
            print(f"  cert_len (EXPECTED_SIZE) = {cert_len} (0x{cert_len:x})")
            print(f"  cert SHA256 (EXPECTED_HASH) = {cert_sha256}")
            found_v2 = True
            cert_size = cert_len
            cert_sha = cert_sha256
        cur += seq_len

    print(f"\nv2_signing_blocks found: {v2_count}")
    if found_v2:
        print(f"\n=== UPDATE Kbuild with: ===")
        print(f'KSU_EXPECTED_SIZE := 0x{cert_size:x}')
        print(f'KSU_EXPECTED_HASH := {cert_sha}')

if __name__ == '__main__':
    main(sys.argv[1])

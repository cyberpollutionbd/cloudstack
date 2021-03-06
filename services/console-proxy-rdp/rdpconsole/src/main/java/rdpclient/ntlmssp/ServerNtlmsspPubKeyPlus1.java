// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package rdpclient.ntlmssp;

import java.util.Arrays;

import rdpclient.ntlmssp.asn1.TSRequest;
import streamer.ByteBuffer;
import streamer.Element;
import streamer.Link;
import streamer.OneTimeSwitch;

public class ServerNtlmsspPubKeyPlus1 extends OneTimeSwitch implements Element {

    protected NtlmState ntlmState;

    public ServerNtlmsspPubKeyPlus1(String id, NtlmState ntlmState) {
        super(id);
        this.ntlmState = ntlmState;
    }

    @Override
    protected void handleOneTimeData(ByteBuffer buf, Link link) {
        TSRequest tsRequest = new TSRequest("TSRequest");
        tsRequest.readTag(buf);

        ByteBuffer encryptedPubKey = tsRequest.pubKeyAuth.value;
        if (encryptedPubKey == null || encryptedPubKey.length == 0)
            throw new RuntimeException("[" + this
                    + "] ERROR: Unexpected message from RDP server. Expected encrypted server public key but got nothing instead. Data: " + buf);

        byte[] decryptedPubKey = ntlmState.ntlm_DecryptMessage(encryptedPubKey.toByteArray());
        //* DEBUG */System.out.println("Decrypted pub key:\n" + new ByteBuffer(decryptedPubKey).dump());

        // Decrease first byte by 1
        decryptedPubKey[0]--;

        // Compare returned value with expected value
        if (!Arrays.equals(decryptedPubKey, ntlmState.subjectPublicKey))
            throw new RuntimeException("[" + this
                    + "] ERROR: Unexpected message from RDP server. Expected encrypted server public key but an unknown response. Encryted key after decryption: "
                    + new ByteBuffer(decryptedPubKey).toPlainHexString());

        buf.unref();
        switchOff(); // Ignore packet
    }
}

/* @formatter:off */

// CredSSP header in BER format:

// 0x30, (byte) 0x82, 0x01, 0x2b, // TAG: [UNIVERSAL 16] (constructed)
// "SEQUENCE" LEN: 299 bytes
// (byte) 0xa0, 0x03, // TAG: [0] (constructed) LEN: 3 bytes
// 0x02, 0x01, 0x03, // TAG: [UNIVERSAL 2] (primitive) "INTEGER" LEN: 1 bytes,
// Version: 0x3
// (byte) 0xa3, (byte) 0x82, 0x01, 0x22, // TAG: [3] (constructed) LEN: 290
// bytes
// 0x04, (byte) 0x82, 0x01, 0x1e, // TAG: [UNIVERSAL 4] (primitive)
// "OCTET STRING" LEN: 286 bytes

// ???

// 0x01, 0x00, 0x00, 0x00, // ???
// (byte) 0x98, (byte) 0xb0, 0x72, 0x48, 0x42, 0x09, (byte) 0xbd, 0x42, 0x00,
// 0x00, 0x00, //
// 0x00, (byte) 0xf6, 0x76, 0x0a, 0x40, (byte) 0xb4, 0x7b, (byte) 0xee, 0x69,
// (byte) 0xfc, (byte) 0x95, 0x2d, 0x5f, 0x6a, (byte) 0xe8, (byte) 0x87, //
// 0x4e, (byte) 0xeb, (byte) 0xae, 0x29, (byte) 0xf2, (byte) 0xde, 0x5e, 0x0a,
// 0x6e, 0x45, (byte) 0xeb, (byte) 0x95, (byte) 0xd9, 0x48, (byte) 0xfc, 0x44,
// //
// 0x7a, 0x34, (byte) 0xb4, (byte) 0xc4, (byte) 0xee, (byte) 0x93, (byte) 0xd2,
// (byte) 0xb4, (byte) 0xe5, (byte) 0xe5, (byte) 0xc1, 0x0f, (byte) 0x9e, 0x3b,
// (byte) 0xce, (byte) 0xaa, //
// 0x76, (byte) 0x9e, 0x2b, 0x33, 0x44, 0x76, 0x2f, 0x2f, (byte) 0x83, 0x34,
// 0x3c, (byte) 0xe9, (byte) 0xc2, (byte) 0xeb, 0x0e, (byte) 0xce, //
// 0x6c, (byte) 0xcd, 0x1c, (byte) 0xae, 0x74, 0x78, 0x3e, (byte) 0x8c, 0x17,
// (byte) 0xb4, 0x39, (byte) 0x9a, 0x21, (byte) 0x99, (byte) 0xde, (byte) 0xae,
// //
// 0x72, 0x23, (byte) 0x94, (byte) 0xc6, (byte) 0xe9, (byte) 0xcb, 0x48, (byte)
// 0xb1, 0x54, 0x20, 0x70, 0x70, (byte) 0xc0, 0x77, 0x10, 0x4b, //
// (byte) 0x8a, (byte) 0xe0, (byte) 0xa0, 0x6c, (byte) 0xb9, 0x65, (byte) 0xfc,
// 0x67, (byte) 0xe3, 0x3b, (byte) 0xb6, 0x46, 0x5e, (byte) 0xaf, (byte) 0xe7,
// (byte) 0x92, //
// 0x6a, (byte) 0xaf, (byte) 0x86, 0x4d, 0x74, 0x33, 0x49, 0x2a, (byte) 0xf0,
// (byte) 0xdd, 0x66, (byte) 0xce, (byte) 0xec, (byte) 0xcc, 0x6b, 0x62, //
// 0x4f, 0x35, (byte) 0xb5, 0x0f, (byte) 0x95, (byte) 0xd7, (byte) 0xf7, (byte)
// 0xf3, 0x4b, 0x59, 0x5f, 0x29, (byte) 0xc9, (byte) 0xc4, (byte) 0xdc, 0x47, //
// (byte) 0xe9, (byte) 0x8d, 0x47, (byte) 0xd2, 0x1d, 0x35, 0x43, (byte) 0xce,
// (byte) 0xff, (byte) 0xd7, 0x6b, 0x28, (byte) 0xd8, 0x06, (byte) 0xe8, (byte)
// 0xba, //
// (byte) 0xf1, 0x4d, (byte) 0xba, 0x43, (byte) 0x8e, 0x64, (byte) 0xba, (byte)
// 0xcd, (byte) 0xcb, (byte) 0xaf, 0x1a, 0x61, (byte) 0xd8, 0x11, 0x19, (byte)
// 0xf7, //
// (byte) 0xae, (byte) 0xfe, (byte) 0x94, 0x48, (byte) 0x8e, (byte) 0x9f, 0x57,
// 0x17, (byte) 0xd2, (byte) 0xa3, (byte) 0xfd, 0x79, (byte) 0xb5, (byte) 0xa3,
// 0x7d, (byte) 0xca, //
// (byte) 0xff, (byte) 0x94, (byte) 0xb5, (byte) 0xb5, 0x03, (byte) 0xf3, 0x13,
// 0x6a, 0x74, 0x7a, (byte) 0xae, (byte) 0x9d, (byte) 0xe9, 0x5c, 0x32, 0x42, //
// 0x37, (byte) 0xa6, (byte) 0xb3, (byte) 0xf5, 0x4b, (byte) 0xaa, 0x22, 0x61,
// (byte) 0xf5, 0x28, 0x5b, 0x41, 0x26, 0x32, 0x63, 0x5f, //
// 0x43, (byte) 0xfd, 0x2e, 0x44, 0x7d, (byte) 0xfb, (byte) 0xb6, 0x09, (byte)
// 0xc5, (byte) 0xc8, 0x33, (byte) 0xbe, (byte) 0x81, 0x08, (byte) 0xd4, 0x5f,
// //
// (byte) 0xad, (byte) 0xee, 0x49, 0x25, 0x62, 0x52, (byte) 0x83, (byte) 0xc1,
// 0x3e, 0x17, 0x5b, (byte) 0xea, 0x4b, (byte) 0x90, 0x62, (byte) 0xf7, //
// 0x4e, 0x28, (byte) 0xfb, (byte) 0xab, (byte) 0x9a, (byte) 0xbc, 0x5e, (byte)
// 0xd4, (byte) 0xd5, 0x56, (byte) 0xf4, 0x4a, 0x2a, 0x7e, (byte) 0xd7, //

/* @formatter:on */

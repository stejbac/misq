/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package misq.p2p.data.storage.mailbox;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import misq.common.security.HybridEncryption;
import misq.common.security.Sealed;
import misq.p2p.data.storage.MetaData;
import misq.p2p.data.storage.auth.AuthenticatedPayload;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PublicKey;

// We want to have fine grained control over mailbox messages.
// As the data is encrypted we could not use it's TTL and we would merge all mailbox message into one storage file.
// By wrapping the sealed data into that NetworkData we can add the fileName and ttl from the unencrypted NetworkData.
@EqualsAndHashCode

public class MailboxPayload implements AuthenticatedPayload {
    public static MailboxPayload createMailboxPayload(MailboxMessage mailboxMessage,
                                                      KeyPair senderKeyPair,
                                                      PublicKey receiverPublicKey)
            throws GeneralSecurityException {
        Sealed sealed = HybridEncryption.encrypt(mailboxMessage.serialize(), receiverPublicKey, senderKeyPair);
        return new MailboxPayload(sealed, mailboxMessage.getMetaData());
    }

    @Getter
    private final Sealed sealed;
    private final MetaData metaData;

    public MailboxPayload(Sealed sealed, MetaData metaData) {
        this.sealed = sealed;
        this.metaData = metaData;
    }

    @Override
    public MetaData getMetaData() {
        return metaData;
    }

    @Override
    public boolean isDataInvalid() {
        return false;
    }

    @Override
    public String toString() {
        return "SealedData{" +
                "\n     sealed=" + sealed +
                ",\n     metaData='" + metaData + '\'' +
                "\n}";
    }
}

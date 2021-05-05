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

package misq.p2p.capability;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import misq.p2p.node.Message;

@EqualsAndHashCode
@Getter
class CapabilityRequest implements Message {
    private final long nonce;
    private final Capability capability;

    CapabilityRequest(Capability capability, long nonce) {
        this.capability = capability;
        this.nonce = nonce;
    }

    @Override
    public String toString() {
        return "HandshakeRequest{" +
                "\n     Capability=" + capability +
                ",\n     nonce=" + nonce +
                "\n}";
    }
}

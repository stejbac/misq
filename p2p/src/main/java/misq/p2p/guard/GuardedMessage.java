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

package misq.p2p.guard;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import misq.p2p.node.Message;

@EqualsAndHashCode
@Getter
public class GuardedMessage implements Message {
    private final Message message;
    private final AccessToken accessToken;

    public GuardedMessage(Message message, AccessToken accessToken) {
        this.message = message;
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return "GuardedMessage{" +
                "\n     message=" + message +
                ",\n     accessToken=" + accessToken +
                "\n}";
    }
}

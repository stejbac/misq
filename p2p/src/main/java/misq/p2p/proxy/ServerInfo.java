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

package misq.p2p.proxy;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import misq.p2p.node.Address;

import java.net.ServerSocket;

@Slf4j
@Getter
public class ServerInfo {
    private final String serverId;
    private ServerSocket serverSocket;
    private Address address;

    public ServerInfo(String serverId, ServerSocket serverSocket, Address address) {
        this.serverId = serverId;
        this.serverSocket = serverSocket;
        this.address = address;
    }

    @Override
    public String toString() {
        return serverId + "-" + address.toString();
    }
}

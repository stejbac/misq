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

package misq.p2p.confidential;

import lombok.extern.slf4j.Slf4j;
import misq.common.util.CollectionUtil;
import misq.p2p.Address;
import misq.p2p.Message;
import misq.p2p.NetworkType;
import misq.p2p.node.Connection;
import misq.p2p.node.MessageListener;
import misq.p2p.node.Node;
import misq.p2p.peers.PeerGroup;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

@Slf4j
public class ConfidentialMessageService implements MessageListener {
    private final Node node;
    private final PeerGroup peerGroup;
    private final Set<MessageListener> messageListeners = new CopyOnWriteArraySet<>();

    public ConfidentialMessageService(Node node, PeerGroup peerGroup) {
        this.node = node;
        this.peerGroup = peerGroup;

        node.addMessageListener(this);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // MessageListener
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onMessage(Message message, Connection connection) {
        if (message instanceof ConfidentialMessage) {
            ConfidentialMessage confidentialMessage = (ConfidentialMessage) message;
            if (confidentialMessage instanceof RelayMessage) {
                RelayMessage relayMessage = (RelayMessage) message;
                Address targetAddress = relayMessage.getTargetAddress();
                send(message, targetAddress);
            } else {
                Message unSealedMessage = unseal(confidentialMessage);
                messageListeners.forEach(listener -> listener.onMessage(unSealedMessage, connection));
            }
        }
    }

    public CompletableFuture<Connection> send(Message message, Address peerAddress) {
        ConfidentialMessage confidentialMessage = seal(message);
        return node.send(confidentialMessage, peerAddress);
    }

    public CompletableFuture<Connection> send(Message message, Connection connection) {
        ConfidentialMessage confidentialMessage = seal(message);
        return node.send(confidentialMessage, connection);
    }

    public CompletableFuture<Connection> relay(Message message, Address peerAddress) {
        Set<Connection> connections = getConnectionsWithSupportedNetwork(peerAddress.getNetworkType());
        Connection outboundConnection = CollectionUtil.getRandomElement(connections);
        if (outboundConnection != null) {
            //todo we need 2 diff. pub keys for encryption here
            ConfidentialMessage inner = seal(message);
            RelayMessage relayMessage = new RelayMessage(inner, peerAddress);
            ConfidentialMessage confidentialMessage = seal(relayMessage);
            return node.send(confidentialMessage, outboundConnection);
        }
        return CompletableFuture.failedFuture(new Exception("No connection supporting that network type found."));
    }

    public void shutdown() {
        node.removeMessageListener(this);
        messageListeners.clear();
    }


    public void addMessageListener(MessageListener messageListener) {
        messageListeners.add(messageListener);
    }

    public void removeMessageListener(MessageListener messageListener) {
        messageListeners.remove(messageListener);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Private
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private Message unseal(ConfidentialMessage confidentialMessage) {
        return confidentialMessage.getMessage();
    }

    private ConfidentialMessage seal(Message message) {
        return new ConfidentialMessage(message);
    }

    private Set<Connection> getConnectionsWithSupportedNetwork(NetworkType networkType) {
        return peerGroup.getConnectedPeerByAddress().stream()
                .filter(peer -> peer.getCapability().getSupportedNetworkTypes().contains(networkType))
                .flatMap(peer -> node.findConnection(peer.getAddress()).stream())
                .collect(Collectors.toSet());
    }
}

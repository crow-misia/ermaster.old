package org.insightech.er.editor.model.diagram_contents.element.connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.RandomUtils;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;

public abstract class ConnectionElement extends AbstractModel {

	private static final long serialVersionUID = -5418951773059063716L;

	public static final String PROPERTY_CHANGE_CONNECTION = "connection";

	public static final String PROPERTY_CHANGE_BEND_POINT = "bendPoint";

	public static final String PROPERTY_CHANGE_CONNECTION_ATTRIBUTE = "connection_attribute";

	private String id;

	protected NodeElement source;

	protected NodeElement target;

	// ベンド・ポイントの位置情報のリスト
	private List<Bendpoint> bendPoints = new ArrayList<Bendpoint>();

	public final String getId() {
		return id;
	}

	public final void setId(final String id) {
		this.id = id;
	}

	public static void setId(final Set<String> check, final ConnectionElement node) {
		String id = node.id;
		while (id == null) {
			id = Long.toString(RandomUtils.nextLong(), Character.MAX_RADIX);
			if (check.add(id)) {
				node.id = id;
				break;
			}
			id = null;
		}
	}

	public NodeElement getSource() {
		return source;
	}

	public void setSource(NodeElement source, final boolean connectionFire) {
		if (this.source != null) {
			this.source.removeOutgoing(this);
		}

		this.source = source;

		if (this.source != null) {
			this.source.addOutgoing(this);
		}

		if (connectionFire) {
			setDirtyForConnection();
		}
	}

	public void setSourceAndTarget(NodeElement source, NodeElement target) {
		this.source = source;
		this.target = target;
	}

	public void setTarget(NodeElement target, final boolean connectionFire) {
		if (this.target != null) {
			this.target.removeIncoming(this);
		}

		this.target = target;
		
		if (this.target != null) {
			this.target.addIncoming(this);
		}

		if (connectionFire) {
			setDirtyForConnection();
		}
	}

	public void setDirtyForConnection() {
		this.firePropertyChange(PROPERTY_CHANGE_CONNECTION, null, source);
	}

	public NodeElement getTarget() {
		return target;
	}

	public void delete() {
		source.removeOutgoing(this);
		target.removeIncoming(this);
	}

	public void connect() {
		if (this.source != null) {
			source.addOutgoing(this);
		}
		if (this.target != null) {
			target.addIncoming(this);
		}
	}

	public void addBendpoint(int index, Bendpoint point, final boolean bendpointFire) {
		bendPoints.add(index, point);
		if (bendpointFire) {
			setDirtyForBendpoint();
		}
	}

	public void setBendpoints(List<Bendpoint> points, final boolean bendpointFire) {
		bendPoints = points;
		if (bendpointFire) {
			setDirtyForBendpoint();
		}
	}

	public List<Bendpoint> getBendpoints() {
		return bendPoints;
	}

	public void removeBendpoint(int index, final boolean bendpointFire) {
		bendPoints.remove(index);
		if (bendpointFire) {
			setDirtyForBendpoint();
		}
	}

	public void replaceBendpoint(int index, Bendpoint point, final boolean bendpointFire) {
		bendPoints.set(index, point);
		if (bendpointFire) {
			setDirtyForBendpoint();
		}
	}

	public void setDirtyForBendpoint() {
		this.firePropertyChange(PROPERTY_CHANGE_BEND_POINT, null, null);
	}

	public void setParentMove() {
		firePropertyChange(PROPERTY_CHANGE_CONNECTION_ATTRIBUTE, null, null);
	}

	/**
	 * 接続を複製します。 接続元と接続先のノードはともに、複製元と同じです。
	 */
	@Override
	public ConnectionElement clone() {
		ConnectionElement clone = (ConnectionElement) super.clone();

		List<Bendpoint> cloneBendPoints = new ArrayList<Bendpoint>(bendPoints.size());
		for (Bendpoint bendPoint : bendPoints) {
			cloneBendPoints.add((Bendpoint) bendPoint.clone());
		}

		clone.bendPoints = cloneBendPoints;

		return clone;
	}
}

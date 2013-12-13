package misc;

import com.billybyte.meteorjava.MeteorBaseListItem;

public class HowTos extends MeteorBaseListItem{
	private final String topic;
	private final String howTo;
	private HowTos(String _id, String userId, String topic, String howTo) {
		super(_id,userId);
		this.topic = topic;
		this.howTo = howTo;
	}
	public String getTopic() {
		return topic;
	}
	public String getHowTo() {
		return howTo;
	}
	@Override
	public String toString() {
		return topic + ", " + howTo + ", " + super.toString();
	}
	
	
	
}

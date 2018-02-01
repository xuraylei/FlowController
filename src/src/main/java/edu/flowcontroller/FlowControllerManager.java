package edu.flowcontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFFactories;
import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFPacketOut;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.action.OFActionOutput;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IPv4AddressWithMask;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.OFVlanVidMatch;
import org.projectfloodlight.openflow.types.TransportPort;
import org.projectfloodlight.openflow.types.VlanVid;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.packet.Ethernet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.flowcontroller.protocol.FCPredicate;
import edu.flowcontroller.protocol.action.EventType;
import edu.flowcontroller.protocol.action.Operation;
import edu.flowcontroller.protocol.action.OperationType;

public class FlowControllerManager implements IOFMessageListener, IFloodlightModule{

	protected IFloodlightProviderService floodlightProvider;
	protected static Logger logger;
	
	protected List<FlowControllerRule> rules;
	protected static MacAddress FCMAC = MacAddress.of("11:11:11:11:11:11");
	
	//use the src mac address as test app id
	public void addFlowControllerRuleTest(){
			MacAddress dstMAC = MacAddress.of("bb:bb:bb:bb:bb:bb");
		
			Object src = new Object("ANY",IPv4Address.of("10.0.0.1"),
					FCMAC, new AppGroup(AppGroup.UNKNOWN), new DeviceGroup(DeviceGroup.UNKNOWN));
			Object dst = new Object("ANY",IPv4Address.of("10.0.0.2"),
					dstMAC, new AppGroup(AppGroup.UNKNOWN), new DeviceGroup(DeviceGroup.UNKNOWN));
			
			/* do not use OF match any more
			//assume OF1.3
			OFFactory my13Factory = OFFactories.getFactory(OFVersion.OF_13);
			Match netMatch = my13Factory.buildMatch()
				    .setExact(MatchField.IN_PORT, OFPort.of(1))
				    .setExact(MatchField.ETH_TYPE, EthType.IPv4)
				    .setMasked(MatchField.IPV4_SRC, IPv4AddressWithMask.of("10.0.0.1/24"))
				   // .setExact(MatchField.IP_PROTO, IpProtocol.TCP)
				    //.setExact(MatchField.TCP_DST, TransportPort.of(80))
				    .build();
			*/
			
			FCMatch match = new FCMatch().setHostName("Turbotax");
			
			
			FCPredicate p = new FCPredicate(EventType.TIME);
			p.addOperation(EventType.TIME, new Operation(OperationType.GEQ, (byte) 8),new Operation(OperationType.LEQ, (byte)10));
			
	}
	
	
	@Override
	public String getName() {
		return "Flow Controller";
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);

		return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider = context
				.getServiceImpl(IFloodlightProviderService.class);
		
		logger = LoggerFactory.getLogger(FlowControllerManager.class);
		
		rules = new ArrayList();
		
	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {

		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
	}

	@Override
	public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		switch (msg.getType()) {
		
		case PACKET_IN:
			return this.processFlowControllerPacket(sw, (OFPacketIn) msg, cntx);
		default:
			break;
		}

		return Command.CONTINUE;
	}
	
	
	
	//handle report action from the data plane
	private Command processFlowControllerPacket(IOFSwitch sw, OFPacketIn msg, FloodlightContext cntx) {
		//TODO: according to app's logic
		
		//FCPolicyRegistration reg_msg = new FCPolicyRegistration();
		
		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx,
                IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		
		//test
		addFlowControllerRuleTest();
		
		for (FlowControllerRule rule: rules){
			
			//according to access control policy for packet forwarding
			OFPort inPort = (msg.getVersion().compareTo(OFVersion.OF_12) < 0 ? msg.getInPort() : msg.getMatch().get(MatchField.IN_PORT));
			Match m = createMatchFromPacket(sw, inPort, cntx);
			
			OFPacketOut.Builder pob = sw.getOFFactory().buildPacketOut();
			
			pob.setBufferId(msg.getBufferId()).setXid(msg.getXid()).
			 	setInPort((msg.getVersion().compareTo(OFVersion.OF_12) < 0 ? msg.getInPort() : msg.getMatch().get(MatchField.IN_PORT)));
			
			//set an OF action, doesn't matter
			OFActionOutput.Builder actionBuilder = sw.getOFFactory().actions().buildOutput();
		    actionBuilder.setPort(OFPort.FLOOD);
		    pob.setActions(Collections.singletonList((OFAction) actionBuilder.build()));
		    
		   /* serialize Flow Controller msg
		    FCPolicyRegistration reg_msg = new FCPolicyRegistration();
		    
		    byte[] byte_reg_msg = null;
			try{
				byte_reg_msg = reg_msg.serialize();
			}catch (IOException e) {
	            logger.error("Failure serialization", e);
	        }
		    pob.setData(byte_reg_msg);
		*/
		    sw.write(pob.build(), null);	
		}
		
	
		
		return Command.CONTINUE;
	}

	
	
	protected Match createMatchFromPacket(IOFSwitch sw, OFPort inPort, FloodlightContext cntx) {
		// The packet in match will only contain the port number.
		// We need to add in specifics for the hosts we're routing between.
		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		VlanVid vlan = VlanVid.ofVlan(eth.getVlanID());
		MacAddress srcMac = eth.getSourceMACAddress();
		MacAddress dstMac = eth.getDestinationMACAddress();

		Match.Builder mb = sw.getOFFactory().buildMatch();
		mb.setExact(MatchField.IN_PORT, inPort)
		.setExact(MatchField.ETH_SRC, srcMac)
		.setExact(MatchField.ETH_DST, dstMac);

		if (!vlan.equals(VlanVid.ZERO)) {
			mb.setExact(MatchField.VLAN_VID, OFVlanVidMatch.ofVlanVid(vlan));
		}

		return mb.build();
	}
}

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
import org.projectfloodlight.openflow.protocol.OFFlowMod;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFPacketOut;
import org.projectfloodlight.openflow.protocol.OFPortDesc;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.action.OFActionOutput;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.DatapathId;
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
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.PortChangeType;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.staticflowentry.StaticFlowEntryPusher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.flowcontroller.protocol.FCPredicate;
import edu.flowcontroller.protocol.action.EventType;
import edu.flowcontroller.protocol.action.FCAction;
import edu.flowcontroller.protocol.action.FCActionControl;
import edu.flowcontroller.protocol.action.FCActionTransition;
import edu.flowcontroller.protocol.action.FCActionTrigger;
import edu.flowcontroller.protocol.action.FCActionType;
import edu.flowcontroller.protocol.action.Operation;
import edu.flowcontroller.protocol.action.OperationType;

public class FlowControllerManager implements IOFMessageListener, IFloodlightModule, IOFSwitchListener{

	protected IFloodlightProviderService floodlightProvider;
	protected IOFSwitchService switchService;
	
	protected static Logger log;

	
	protected List<FlowControllerPolicy> policies;
	
	protected static MacAddress FCMAC = MacAddress.of("11:11:11:11:11:11");
	
	private  DatapathId targerTestSW = DatapathId.of("00:00:00:00:00:00:00:01");
	
	
	//use the src mac address as test app id
	public void addPolicyTest(){
			// one stage policy
			FlowControllerPolicy fcp = new FlowControllerPolicy((byte)1);
			
			IPv4Address srcIP = IPv4Address.of("10.0.0.1");
			IPv4Address dstIP = IPv4Address.of("10.0.0.2");
			IPv4Address redirectIP = IPv4Address.of("10.0.0.3");
			MacAddress srcMAC = MacAddress.of("aa:aa:aa:aa:aa:aa");
			MacAddress dstMAC = MacAddress.of("bb:bb:bb:bb:bb:bb");
			MacAddress redirectMAC = MacAddress.of("cc:cc:cc:cc:cc:cc");
			
			//rules for stage 1
			byte stage1 = 1;
			
			Object srcObj1 = new Object(Object.ANY,srcIP,
					srcMAC, new AppGroup(AppGroup.UNKNOWN), new DeviceGroup(DeviceGroup.UNKNOWN));
			Object dstObj1 = new Object(Object.NET,dstIP,
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
			
			
			FCMatch match = new FCMatch(FCMatch.HOST_MATCH_ONLY);
			match.setHostMatch("Turbotax", FCMatch.FILE_OP_WRITE, 100);
			match.setNetMatch(-1, -1, (byte)-1, (short)-1, (short)-1);
			
			FCPredicate p1 = new FCPredicate(EventType.TIME);
			p1.addOperation(new Operation(OperationType.GEQ, (byte) 8));
			p1.addOperation(new Operation(OperationType.LEQ, (byte)10));
			//Latitude and longitude for location
			FCPredicate p2 = new FCPredicate(EventType.LOCATION);
			p2.addOperation(new Operation(OperationType.EQUAL, 
					(byte) 11));
			p2.addOperation(new Operation(OperationType.EQUAL, (byte) 44));
			
			FCActionTransition action11 = new FCActionTransition();
			action11.setNextStage((byte)2);
			
			FCActionControl action12 = new FCActionControl();
			action12.setActionType(FCActionControl.REDIRECT);
			action12.setActionType(FCActionControl.REPORT);
			action12.setRedirectionDevice(redirectIP, redirectMAC);
			
			FCActionTrigger action13 = new FCActionTrigger();
			action13.setTriggerType(FCActionTrigger.IMMEDIATE);
			
			FlowControllerRule rule1 = new FlowControllerRule(srcObj1, dstObj1, match);
			rule1.addPredicate(p1);
			rule1.addPredicate(p2);
			rule1.addAction(action11);
			rule1.addAction(action12);
			rule1.addAction(action13);
			
			fcp.addPolicy((byte)1, rule1);

			policies.add(fcp);
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
		 return (type.equals(OFType.PACKET_IN) && name.equals("forwarding"));
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
		l.add(IOFSwitchService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider = context
				.getServiceImpl(IFloodlightProviderService.class);
		switchService = context.getServiceImpl(IOFSwitchService.class);
		log = LoggerFactory.getLogger(FlowControllerManager.class);
		
		policies = new ArrayList<FlowControllerPolicy>();
		
		//for test
		addPolicyTest();
		
	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {

		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		switchService.addOFSwitchListener(this);

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
	

	//handle report from the data plane
	private Command processFlowControllerPacket(IOFSwitch sw, OFPacketIn msg, FloodlightContext cntx) {
	
		//handle policy request
		//check if the message is for flow controller
		if ( msg.getXid() != 1) {
			return Command.CONTINUE;
		}
		
		
		
		/*
		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx,
                IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		
		//test
		//addFlowControllerRuleTest();
		
		for (FlowControllerPolicy policy: policies){
			
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
		
		    sw.write(pob.build(), null);	
		}
		
		*/
		
		return Command.STOP;
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


	@Override
	public void switchAdded(DatapathId sid) {
		log.info("switch " + sid + " is added.");
		if (sid.equals(targerTestSW)) {
			log.info("Install policies into sw" + sid.toString());
			installFlowControllerPolicies(sid);
		}
		
	}

	private void installFlowControllerPolicies(DatapathId switchId) {
		IOFSwitch sw = switchService.getSwitch(targerTestSW);
		if (sw == null) {
			log.error("Cannot find the target switch for Flow Controller policies");
			return;
		}
		
		for (FlowControllerPolicy p: policies){
			OFPacketOut.Builder pob = sw.getOFFactory().buildPacketOut();
			
			//set an OF action, doesn't matter
			OFActionOutput.Builder actionBuilder = sw.getOFFactory().actions().buildOutput();
		    actionBuilder.setPort(OFPort.FLOOD);
		    pob.setActions(Collections.singletonList((OFAction) actionBuilder.build()));
		    
		    //use xid 1 for policy installation message, can be changed later
		    pob.setXid(1);
		    
		    //serialize Flow Controller message
		    byte[] data;
		  
			try {
				data = p.serialize();
				pob.setData(data);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			sw.write(pob.build());
		}
	}
	

	@Override
	public void switchRemoved(DatapathId switchId) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void switchActivated(DatapathId switchId) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void switchPortChanged(DatapathId switchId, OFPortDesc port, PortChangeType type) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void switchChanged(DatapathId switchId) {
		// TODO Auto-generated method stub
		
	}
}

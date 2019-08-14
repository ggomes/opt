package opt.data;

import jaxb.Link;
import jaxb.Roadparam;
import profiles.Profile1D;

public abstract class LinkFreewayOrConnector extends AbstractLink {

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public LinkFreewayOrConnector(Link link, Roadparam rp) {
        super(link, rp);
    }

    public LinkFreewayOrConnector(Long id, String name, Long start_node_id, Long end_node_id, Integer full_lanes, Integer managed_lanes, Integer aux_lanes, Float length, Float capacity_vphpl, Float jam_density_vpkpl, Float ff_speed_kph, Segment mysegment) {
        super(id, name, start_node_id, end_node_id, full_lanes, managed_lanes, aux_lanes, length, capacity_vphpl, jam_density_vpkpl, ff_speed_kph, mysegment);
    }

//    @Override
//    protected void delete(){
//        if(up_link!=null)
//            up_link.dn_link = null;
//        if(dn_link!=null)
//            dn_link.up_link = null;
//        demands = null;
//        splits = null;
//    }

    /////////////////////////////////////
    // demands and splits
    /////////////////////////////////////

    @Override
    public void set_demand_vph(Long comm_id, Profile1D profile) throws Exception {
        this.demands.put(comm_id,profile);
    }

}
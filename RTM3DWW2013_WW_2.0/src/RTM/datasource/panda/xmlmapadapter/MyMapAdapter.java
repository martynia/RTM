/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.datasource.panda.xmlmapadapter;

/**
 *
 * @author martynia
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class MyMapAdapter extends XmlAdapter<MyMapType, Map> {

    @Override
    public MyMapType marshal(Map arg0) throws Exception {
        MyMapType myMapType = new MyMapType();

        for (Entry<String, SiteJobs> entry : (Set<Map.Entry<String,SiteJobs>>)arg0.entrySet()) {
            MyMapEntryType myMapEntryType =
                    new MyMapEntryType();
            myMapEntryType.key = entry.getKey();
            myMapEntryType.value = entry.getValue();
            myMapType.entry.add(myMapEntryType);
        }
        return myMapType;
    }

    @Override
    public Map unmarshal(MyMapType arg0) throws Exception {
        HashMap<String,SiteJobs> hashMap = new HashMap<String,SiteJobs>();
        for (MyMapEntryType myEntryType : (List<MyMapEntryType>)arg0.entry) {
            hashMap.put(myEntryType.key, myEntryType.value);
        }
        return hashMap;
    }
}

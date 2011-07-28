
package info.semanticsoftware.semassist.server;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for userContext complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="userContext">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mUserLanguages" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="mDocLang" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "userContext", propOrder = {
    "mUserLanguages",
    "mDocLang"
})
public class UserContext {

    @XmlElement(nillable = true)
    protected List<String> mUserLanguages;
    protected String mDocLang;

    /**
     * Gets the value of the mUserLanguages property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mUserLanguages property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMUserLanguages().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getMUserLanguages() {
        if (mUserLanguages == null) {
            mUserLanguages = new ArrayList<String>();
        }
        return this.mUserLanguages;
    }

    /**
     * Gets the value of the mDocLang property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMDocLang() {
        return mDocLang;
    }

    /**
     * Sets the value of the mDocLang property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMDocLang(String value) {
        this.mDocLang = value;
    }

}

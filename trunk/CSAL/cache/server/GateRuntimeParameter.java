
package info.semanticsoftware.semassist.server;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for gateRuntimeParameter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gateRuntimeParameter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="booleanValue" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="corpusValue" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="defaultValueString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="doubleValue" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="intValue" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optional" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="PRName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paramName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pipelineName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="stringValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="urlValue" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gateRuntimeParameter", propOrder = {
    "booleanValue",
    "corpusValue",
    "defaultValueString",
    "doubleValue",
    "intValue",
    "label",
    "optional",
    "prName",
    "paramName",
    "pipelineName",
    "stringValue",
    "type",
    "urlValue"
})
public class GateRuntimeParameter {

    protected Boolean booleanValue;
    @XmlElement(nillable = true)
    protected List<Object> corpusValue;
    protected String defaultValueString;
    protected Double doubleValue;
    protected Integer intValue;
    protected String label;
    protected boolean optional;
    @XmlElement(name = "PRName")
    protected String prName;
    protected String paramName;
    protected String pipelineName;
    protected String stringValue;
    protected String type;
    @XmlSchemaType(name = "anyURI")
    protected String urlValue;

    /**
     * Gets the value of the booleanValue property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBooleanValue() {
        return booleanValue;
    }

    /**
     * Sets the value of the booleanValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBooleanValue(Boolean value) {
        this.booleanValue = value;
    }

    /**
     * Gets the value of the corpusValue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the corpusValue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCorpusValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getCorpusValue() {
        if (corpusValue == null) {
            corpusValue = new ArrayList<Object>();
        }
        return this.corpusValue;
    }

    /**
     * Gets the value of the defaultValueString property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultValueString() {
        return defaultValueString;
    }

    /**
     * Sets the value of the defaultValueString property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultValueString(String value) {
        this.defaultValueString = value;
    }

    /**
     * Gets the value of the doubleValue property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getDoubleValue() {
        return doubleValue;
    }

    /**
     * Sets the value of the doubleValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setDoubleValue(Double value) {
        this.doubleValue = value;
    }

    /**
     * Gets the value of the intValue property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIntValue() {
        return intValue;
    }

    /**
     * Sets the value of the intValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIntValue(Integer value) {
        this.intValue = value;
    }

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

    /**
     * Gets the value of the optional property.
     * 
     */
    public boolean isOptional() {
        return optional;
    }

    /**
     * Sets the value of the optional property.
     * 
     */
    public void setOptional(boolean value) {
        this.optional = value;
    }

    /**
     * Gets the value of the prName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRName() {
        return prName;
    }

    /**
     * Sets the value of the prName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRName(String value) {
        this.prName = value;
    }

    /**
     * Gets the value of the paramName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParamName() {
        return paramName;
    }

    /**
     * Sets the value of the paramName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParamName(String value) {
        this.paramName = value;
    }

    /**
     * Gets the value of the pipelineName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPipelineName() {
        return pipelineName;
    }

    /**
     * Sets the value of the pipelineName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPipelineName(String value) {
        this.pipelineName = value;
    }

    /**
     * Gets the value of the stringValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStringValue() {
        return stringValue;
    }

    /**
     * Sets the value of the stringValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStringValue(String value) {
        this.stringValue = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the urlValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrlValue() {
        return urlValue;
    }

    /**
     * Sets the value of the urlValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrlValue(String value) {
        this.urlValue = value;
    }

}

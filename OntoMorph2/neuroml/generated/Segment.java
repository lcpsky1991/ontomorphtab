//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.04.03 at 06:02:34 PM PDT 
//


package neuroml.generated;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Defines the smallest unit within a possibly branching structure, such as a dendrite or axon. The parent attribute is used to define connectivity. A segment would be mapped to a compartment in a compartmental modelling application such as GENESIS
 * 
 * <p>Java class for Segment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Segment">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="proximal" type="{http://morphml.org/metadata/schema}Point" minOccurs="0"/>
 *         &lt;element name="distal" type="{http://morphml.org/metadata/schema}Point"/>
 *         &lt;element name="properties" type="{http://morphml.org/metadata/schema}Properties" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="cable" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="id" use="required" type="{http://morphml.org/morphml/schema}SegmentIdInCell" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="parent" type="{http://morphml.org/morphml/schema}SegmentIdInCell" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Segment", namespace = "http://morphml.org/morphml/schema", propOrder = {
    "proximal",
    "distal",
    "properties"
})
public class Segment {

    @XmlElement(namespace = "http://morphml.org/morphml/schema")
    protected Point proximal;
    @XmlElement(namespace = "http://morphml.org/morphml/schema", required = true)
    protected Point distal;
    @XmlElement(namespace = "http://morphml.org/morphml/schema")
    protected Properties properties;
    @XmlAttribute
    protected BigInteger cable;
    @XmlAttribute(required = true)
    protected BigInteger id;
    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected BigInteger parent;

    /**
     * Gets the value of the proximal property.
     * 
     * @return
     *     possible object is
     *     {@link Point }
     *     
     */
    public Point getProximal() {
        return proximal;
    }

    /**
     * Sets the value of the proximal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Point }
     *     
     */
    public void setProximal(Point value) {
        this.proximal = value;
    }

    /**
     * Gets the value of the distal property.
     * 
     * @return
     *     possible object is
     *     {@link Point }
     *     
     */
    public Point getDistal() {
        return distal;
    }

    /**
     * Sets the value of the distal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Point }
     *     
     */
    public void setDistal(Point value) {
        this.distal = value;
    }

    /**
     * Gets the value of the properties property.
     * 
     * @return
     *     possible object is
     *     {@link Properties }
     *     
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     * 
     * @param value
     *     allowed object is
     *     {@link Properties }
     *     
     */
    public void setProperties(Properties value) {
        this.properties = value;
    }

    /**
     * Gets the value of the cable property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCable() {
        return cable;
    }

    /**
     * Sets the value of the cable property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCable(BigInteger value) {
        this.cable = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setId(BigInteger value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the parent property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getParent() {
        return parent;
    }

    /**
     * Sets the value of the parent property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setParent(BigInteger value) {
        this.parent = value;
    }

}
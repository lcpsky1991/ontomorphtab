//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.04.03 at 06:02:34 PM PDT 
//


package neuroml.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * What causes the gate to open and close. A dependence on potential difference, 
 *             or a voltage and (ion) concentration dependence
 * 
 * <p>Java class for Transition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Transition">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="voltage_gate" type="{http://morphml.org/channelml/schema}VoltageGate"/>
 *         &lt;element name="voltage_conc_gate" type="{http://morphml.org/channelml/schema}VoltageConcGate"/>
 *       &lt;/choice>
 *       &lt;attribute name="src" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Transition", propOrder = {
    "voltageGate",
    "voltageConcGate"
})
public class Transition {

    @XmlElement(name = "voltage_gate")
    protected VoltageGate voltageGate;
    @XmlElement(name = "voltage_conc_gate")
    protected VoltageConcGate voltageConcGate;
    @XmlAttribute
    protected String src;
    @XmlAttribute
    protected String target;

    /**
     * Gets the value of the voltageGate property.
     * 
     * @return
     *     possible object is
     *     {@link VoltageGate }
     *     
     */
    public VoltageGate getVoltageGate() {
        return voltageGate;
    }

    /**
     * Sets the value of the voltageGate property.
     * 
     * @param value
     *     allowed object is
     *     {@link VoltageGate }
     *     
     */
    public void setVoltageGate(VoltageGate value) {
        this.voltageGate = value;
    }

    /**
     * Gets the value of the voltageConcGate property.
     * 
     * @return
     *     possible object is
     *     {@link VoltageConcGate }
     *     
     */
    public VoltageConcGate getVoltageConcGate() {
        return voltageConcGate;
    }

    /**
     * Sets the value of the voltageConcGate property.
     * 
     * @param value
     *     allowed object is
     *     {@link VoltageConcGate }
     *     
     */
    public void setVoltageConcGate(VoltageConcGate value) {
        this.voltageConcGate = value;
    }

    /**
     * Gets the value of the src property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSrc() {
        return src;
    }

    /**
     * Sets the value of the src property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSrc(String value) {
        this.src = value;
    }

    /**
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTarget(String value) {
        this.target = value;
    }

}

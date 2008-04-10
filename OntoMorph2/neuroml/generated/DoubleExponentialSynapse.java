//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.04.03 at 06:02:34 PM PDT 
//


package neuroml.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A basic synaptic mechanism with a double exponential conductance time course. This mechanism maps 
 *             easily on to mechanisms in both NEURON (Exp2Syn) and GENESIS (synchan)
 * 
 * <p>Java class for DoubleExponentialSynapse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DoubleExponentialSynapse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://morphml.org/metadata/schema}metadata"/>
 *       &lt;/sequence>
 *       &lt;attribute name="decay_time" use="required" type="{http://morphml.org/biophysics/schema}TimeConstantValue" />
 *       &lt;attribute name="max_conductance" use="required" type="{http://morphml.org/biophysics/schema}ConductanceValue" />
 *       &lt;attribute name="reversal_potential" use="required" type="{http://morphml.org/biophysics/schema}VoltageValue" />
 *       &lt;attribute name="rise_time" use="required" type="{http://morphml.org/biophysics/schema}TimeConstantValue" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DoubleExponentialSynapse", propOrder = {
    "notes",
    "properties",
    "annotation",
    "group"
})
public class DoubleExponentialSynapse {

    @XmlElement(namespace = "http://morphml.org/metadata/schema")
    protected String notes;
    @XmlElement(namespace = "http://morphml.org/metadata/schema")
    protected Properties properties;
    @XmlElement(namespace = "http://morphml.org/metadata/schema")
    protected Annotation annotation;
    @XmlElement(namespace = "http://morphml.org/metadata/schema")
    protected List<String> group;
    @XmlAttribute(name = "decay_time", required = true)
    protected double decayTime;
    @XmlAttribute(name = "max_conductance", required = true)
    protected double maxConductance;
    @XmlAttribute(name = "reversal_potential", required = true)
    protected double reversalPotential;
    @XmlAttribute(name = "rise_time", required = true)
    protected double riseTime;

    /**
     * Gets the value of the notes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the value of the notes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotes(String value) {
        this.notes = value;
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
     * Gets the value of the annotation property.
     * 
     * @return
     *     possible object is
     *     {@link Annotation }
     *     
     */
    public Annotation getAnnotation() {
        return annotation;
    }

    /**
     * Sets the value of the annotation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Annotation }
     *     
     */
    public void setAnnotation(Annotation value) {
        this.annotation = value;
    }

    /**
     * Gets the value of the group property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the group property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getGroup() {
        if (group == null) {
            group = new ArrayList<String>();
        }
        return this.group;
    }

    /**
     * Gets the value of the decayTime property.
     * 
     */
    public double getDecayTime() {
        return decayTime;
    }

    /**
     * Sets the value of the decayTime property.
     * 
     */
    public void setDecayTime(double value) {
        this.decayTime = value;
    }

    /**
     * Gets the value of the maxConductance property.
     * 
     */
    public double getMaxConductance() {
        return maxConductance;
    }

    /**
     * Sets the value of the maxConductance property.
     * 
     */
    public void setMaxConductance(double value) {
        this.maxConductance = value;
    }

    /**
     * Gets the value of the reversalPotential property.
     * 
     */
    public double getReversalPotential() {
        return reversalPotential;
    }

    /**
     * Sets the value of the reversalPotential property.
     * 
     */
    public void setReversalPotential(double value) {
        this.reversalPotential = value;
    }

    /**
     * Gets the value of the riseTime property.
     * 
     */
    public double getRiseTime() {
        return riseTime;
    }

    /**
     * Sets the value of the riseTime property.
     * 
     */
    public void setRiseTime(double value) {
        this.riseTime = value;
    }

}
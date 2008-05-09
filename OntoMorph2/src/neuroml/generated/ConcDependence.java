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
import javax.xml.bind.annotation.XmlType;


/**
 * Specification of the concentration dependence of a gate
 * 
 * <p>Java class for ConcDependence complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConcDependence">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="ion" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="max_conc" use="required" type="{http://morphml.org/biophysics/schema}ConcentrationValue" />
 *       &lt;attribute name="min_conc" use="required" type="{http://morphml.org/biophysics/schema}ConcentrationValue" />
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="variable_name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConcDependence")
public class ConcDependence {

    @XmlAttribute
    protected String ion;
    @XmlAttribute(name = "max_conc", required = true)
    protected double maxConc;
    @XmlAttribute(name = "min_conc", required = true)
    protected double minConc;
    @XmlAttribute(required = true)
    protected String name;
    @XmlAttribute(name = "variable_name", required = true)
    protected String variableName;

    /**
     * Gets the value of the ion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIon() {
        return ion;
    }

    /**
     * Sets the value of the ion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIon(String value) {
        this.ion = value;
    }

    /**
     * Gets the value of the maxConc property.
     * 
     */
    public double getMaxConc() {
        return maxConc;
    }

    /**
     * Sets the value of the maxConc property.
     * 
     */
    public void setMaxConc(double value) {
        this.maxConc = value;
    }

    /**
     * Gets the value of the minConc property.
     * 
     */
    public double getMinConc() {
        return minConc;
    }

    /**
     * Sets the value of the minConc property.
     * 
     */
    public void setMinConc(double value) {
        this.minConc = value;
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
     * Gets the value of the variableName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVariableName() {
        return variableName;
    }

    /**
     * Sets the value of the variableName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVariableName(String value) {
        this.variableName = value;
    }

}
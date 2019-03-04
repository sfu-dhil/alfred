/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nines.alfred.vsm;

import ca.nines.alfred.entity.Document;
import edu.stanford.nlp.util.TwoDimensionalMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mjoyce
 */
public class Model {

    private List<String> terms;

    private List<Document> documents;

    private TwoDimensionalMap<String, Document, Double> data;

}

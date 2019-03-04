/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nines.alfred.entity;

import java.util.List;

/**
 *
 * @author michael
 */
interface NGrams {

    public List<String> ngrams(int length);

}

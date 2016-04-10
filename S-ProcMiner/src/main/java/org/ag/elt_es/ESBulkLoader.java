/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ag.elt_es;

import edu.emory.mathcs.backport.java.util.Arrays;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Index;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ahmed
 */
public class ESBulkLoader {

    JestClient client;
    String defaultIndex = null;

    public ESBulkLoader(String[] es_hosts) {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig.Builder(Arrays.asList(es_hosts))
                .multiThreaded(true)
                .build());
        client = factory.getObject();
    }
    
    public void shutdownClient(){
        this.client.shutdownClient();
    }
    /*
        Sending JSON having index and type as attribue ()
     */
    public void SyncCSVIntegration(Iterator<String> it, String[] time_fields, String[] event_attributes, int bulkSize, String index, String type) {
        List<Index> bulk_buffer = new ArrayList<>();
        Random r = new Random();
        while (it.hasNext()) {
            Optional<String> op = Optional.empty();
            Index idx_json = IndexObjBuilder.buildIndexFromCSVEntry(it.next(), time_fields, event_attributes, index, type, op);
            if (idx_json != null) {
                bulk_buffer.add(idx_json);
            } else {
                /* HANDLING BUILDING ERRORS */
                System.out.println("NULLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
            }

            if (bulk_buffer.size() == bulkSize) {
                Bulk bulk = new Bulk.Builder().addAction(bulk_buffer).build();
                try {
                    BulkResult blk_result = client.execute(bulk);
                    handleFailedItems(blk_result);
                } catch (IOException ex) {
                    Logger.getLogger(ESBulkLoader.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Salut les erreurs");
                }
                bulk_buffer = new ArrayList<>();
            }
        }
        // Sending the last bulk
        if (!bulk_buffer.isEmpty()) {
            Bulk bulk = new Bulk.Builder().addAction(bulk_buffer).build();
            try {
                BulkResult blk_result = client.execute(bulk);
                handleFailedItems(blk_result);
            } catch (IOException ex) {
                Logger.getLogger(ESBulkLoader.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Salut les erreurs");
            }
        }
    }

    /*
        Sending JSON having index and type as attribue ()
     */
    public void AsyncCSVIntegration(Iterator<String> it, String[] time_fields, String[] event_attributes, int bulkSize, String index, String type) {
        List<Index> bulk_buffer = new ArrayList<>();
        Random r = new Random();
        while (it.hasNext()) {
            Optional<String> op = Optional.empty();
            Index idx_json = IndexObjBuilder.buildIndexFromCSVEntry(it.next(), time_fields, event_attributes, index, type, op);
            if (idx_json != null) {
                bulk_buffer.add(idx_json);
            } else {
                /* HANDLING BUILDING ERRORS */
                System.out.println("NULLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
            }

            if (bulk_buffer.size() == bulkSize) {
                Bulk bulk = new Bulk.Builder().addAction(bulk_buffer).build();
                client.executeAsync(bulk, new JestResultHandler<JestResult>() {
                    @Override
                    public void completed(JestResult result) {
                        handleFailedItems((BulkResult) result);
                    }

                    @Override
                    public void failed(Exception ex) {
                        System.out.println(ex.getMessage()) ; 
                    }
                });
                bulk_buffer = new ArrayList<>();
            }
        }
        // Sending the last bulk
        if (!bulk_buffer.isEmpty()) {
            Bulk bulk = new Bulk.Builder().addAction(bulk_buffer).build();
            client.executeAsync(bulk, new JestResultHandler<JestResult>() {
                @Override
                public void completed(JestResult result) {
                    handleFailedItems((BulkResult) result);
                }

                @Override
                public void failed(Exception ex) {

                }
            });
        }
    }

    public void handleFailedItems(BulkResult result) {
        System.out.println(result.getFailedItems().size());
    }
    
    
    
}

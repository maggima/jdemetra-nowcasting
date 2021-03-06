/*
 * Copyright 2013-2014 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.tss.dfm;

import ec.tss.Ts;
import ec.tss.documents.VersionedDocument;
import ec.tstoolkit.MetaData;
import ec.tstoolkit.ParameterType;
import ec.tstoolkit.algorithm.CompositeResults;
import ec.tstoolkit.dfm.DfmNews;
import ec.tstoolkit.dfm.DfmSpec;
import ec.tstoolkit.timeseries.information.TsInformationSet;
import java.util.Date;

/**
 *
 * @author Jean Palate
 */
public class VersionedDfmDocument extends VersionedDocument<DfmSpec, Ts[], CompositeResults, DfmDocument>
        implements Cloneable {

    public VersionedDfmDocument() {
        super(new DfmDocument());
    }

    @Override
    public VersionedDfmDocument clone() {
        try {
            VersionedDfmDocument doc = (VersionedDfmDocument) super.clone();
            doc.setCurrent(getCurrent().clone());
            doc.clearVersions(0);
            for (int i = 0; i < getVersionCount(); ++i) {
                doc.add(getVersion(i).clone());
            }
            return doc;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    @Override
    protected DfmDocument newDocument(DfmDocument doc) {
        if (doc != null) {
            DfmDocument ndoc=doc.clone();
            DfmSpec spec = ndoc.getSpecification();
            spec.getModelSpec().setParameterType(ParameterType.Initial);
            spec.getEstimationSpec().disable();
            ndoc.setLocked(true);
            return ndoc;
        }
        else
            return new DfmDocument();
     }

    @Override
    protected DfmDocument restore(DfmDocument document) {
        document.setLocked(false);
        document.unfreezeTs();
        document.getMetaData().remove(MetaData.DATE);
        return document;
    }

    @Override
    protected DfmDocument archive(DfmDocument document) {
        document.freezeTs();
        document.getMetaData().put(MetaData.DATE, new Date().toString());
        document.setLocked(true);
        return document;
    }

    public void refreshData() {
        DfmDocument current = getCurrent();
        if (current != null) {
            boolean locked = current.isLocked();
            if (locked) {
                current.setLocked(false);
            }
            current.unfreezeTs();
            if (locked) {
                current.setLocked(true);
            }
        }
    }
    
    public DfmNews getRevisionsNews(int ver){
        DfmDocument refdoc=null;
        if (ver == -1)
            refdoc=this.getLastVersion();
        else
            refdoc=this.getVersion(ver);
        if (refdoc == null)
            return null;
        DfmResults cur=this.getCurrent().getDfmResults(),
                prev=refdoc.getDfmResults();
        TsInformationSet curinfo = cur.getInput();
        TsInformationSet previnfo = prev.getInput();
        TsInformationSet revinfo = previnfo.revisedData(curinfo);
        DfmNews news=new DfmNews(cur.getModel());
        
        if (! news.process(previnfo, revinfo))
            return null;
        return news;
    }

    public DfmNews getNews(int ver){
        DfmDocument refdoc;
        if (ver == -1)
            refdoc=this.getLastVersion();
        else
            refdoc=this.getVersion(ver);
        if (refdoc == null)
            return null;
        DfmResults cur=this.getCurrent().getDfmResults(),
                prev=refdoc.getDfmResults();
        TsInformationSet curinfo = cur.getInput();
        TsInformationSet previnfo = prev.getInput();
        TsInformationSet revinfo = previnfo.revisedData(curinfo);
        DfmNews news=new DfmNews(cur.getModel());
        
        if (! news.process(revinfo, curinfo))
            return null;
        return news;
    }
    
    public DfmNews getNewsAndRevisions(int ver){
        DfmDocument refdoc;
        if (ver == -1)
            refdoc=this.getLastVersion();
        else
            refdoc=this.getVersion(ver);
        if (refdoc == null)
            return null;
        DfmResults cur=this.getCurrent().getDfmResults(),
                prev=refdoc.getDfmResults();
        TsInformationSet curinfo=cur.getInput(), previnfo=prev.getInput();
        DfmNews news=new DfmNews(cur.getModel());
        
        if (! news.process(previnfo, curinfo))
            return null;
        return news;
    }
    
    public void unlockModel() {
        DfmDocument current = getCurrent();
        if (current != null) {
            current.setLocked(false);
        }
    }

}

package com.scut.jsj.beans.factory.support;

import com.scut.jsj.beans.factory.xml.XmlParser;
import com.scut.jsj.core.io.Resource;
import com.scut.jsj.beans.factory.config.BeanDefinition;
import com.scut.jsj.exception.BeanDefinitionStoreException;
import com.scut.jsj.util.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.util.Map;
import java.util.Set;

/**
 * @author jsj
 * @since 2018-4-11
 * BeanDefinitionReader的真正实现类，继承了AbstractBeanDefinitionReader抽象类
 */
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

    protected final Log logger = LogFactory.getLog(this.getClass());
    //用于暂时保存bean对应的Resource对象,利用ThreadLocal方式存于当前线程中
    private ThreadLocal<Set<Resource>> resourcesCurrentlyBeingLoaded;

    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
        Assert.notNull(resource, "EncodedResource must not be null");
        //此处应该被日志记录
        logger.info("Loading XML bean definitions from " + resource.getDescription());
//        Set<Resource> currentResources = this.resourcesCurrentlyBeingLoaded.get();
//        //首次设置
//        if (currentResources == null) {
//            currentResources = new HashSet<>(4);
//            this.resourcesCurrentlyBeingLoaded.set(currentResources);
//        }
//        //将需要被转换的Resource对象暂存入resourcesCurrentlyBeingLoaded的set中
//        if (!currentResources.add(resource)) {
//            throw new BeanDefinitionStoreException("Detected cyclic loading of " + resource
//                    + " - check your import definitions!");
//        }
        //如果存入set成功就执行doLoadBeanDefinitions(Resource iotest)：此处与spring的操作不同，做了简化，直接进入解析
        return doLoadBeanDefinitions(resource);
    }

    protected int doLoadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
        try {
            //spring使用了DocumentLoader得到Document对象，本项目此处进行简化(运用了DOM4j进行解析)
            Document doc = this.doLoadDocument(resource);
            //进行注册
            return this.registerBeanDefinitions(doc, resource);
        } catch (BeanDefinitionStoreException var4) {
            throw var4;
        } catch (Throwable var9) {
            throw new BeanDefinitionStoreException(resource.getDescription(), "Unexpected exception parsing XML document from " + resource, var9);
        }
    }

    /**
     * 根据Resource载入Document对象
     *
     * @param resource
     * @return
     * @throws Exception
     */
    private Document doLoadDocument(Resource resource) throws Exception {
        //创建SAXReader对象
        SAXReader reader = new SAXReader();
        //读取文件 转换成Document
        return reader.read(resource.getFile());
    }

    /**
     * 根据Resource向BeanDefinitionRegistry注册所有解析出来的BeanDefinition
     *
     * @param doc
     * @param resource
     * @return
     */
    private int registerBeanDefinitions(Document doc, Resource resource) {
        //获得注册表中注册的Bean数量
        int countBefore = this.getRegistry().getBeanDefinitionCount();
        try {
            //此处进行简化，省去了BeanDefinitionDocumentReader接口及其默认实现类，直接通过XmlParser的静态方法进行解析
            Map<String, BeanDefinition> beanDefinitions = XmlParser.parser(doc, resource);
            //这里要将BeanDefinition注册到beanDefiitionRegistry中
            for (Map.Entry<String, BeanDefinition> entry : beanDefinitions.entrySet()) {
                this.getRegistry().registerBeanDefinition(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回所解析的bean数目
        return this.getRegistry().getBeanDefinitionCount() - countBefore;
    }
}

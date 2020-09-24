[#assign referencePath = content.referencePath!]
[#if referencePath?has_content]
    [@cms.component content=cmsfn.contentByPath(referencePath) /]
[/#if]
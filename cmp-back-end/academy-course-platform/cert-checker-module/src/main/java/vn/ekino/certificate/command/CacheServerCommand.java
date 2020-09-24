package vn.ekino.certificate.command;

import info.magnolia.context.Context;
import info.magnolia.module.cache.commands.FlushCachesCommand;
import info.magnolia.module.cache.inject.CacheFactoryProvider;
import lombok.extern.slf4j.Slf4j;
import vn.ekino.certificate.service.RedisService;

import javax.inject.Inject;

@Slf4j
public class CacheServerCommand extends FlushCachesCommand {
    private final RedisService redisService;

    @Inject
    public CacheServerCommand(CacheFactoryProvider cacheFactoryProvider, RedisService redisService) {
        super(cacheFactoryProvider);
        this.redisService = redisService;
    }

    @Override
    public boolean execute(Context context) {
        redisService.cacheData();
        return true;
    }
}

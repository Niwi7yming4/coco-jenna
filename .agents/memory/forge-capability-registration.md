---
name: Forge 1.20.1 Capability Registration
description: How to correctly register capabilities in Forge 47.x (MC 1.20.1) vs older patterns.
---

## The rule

In Forge 47.x for MC 1.20.1, capabilities MUST be registered via `RegisterCapabilitiesEvent` on the **mod event bus**, not via the old `CapabilityManager.get()` call inside `FMLCommonSetupEvent`.

**Why:** The `@CapabilityInject` approach was deprecated/changed in Forge 1.20.x. Without firing `RegisterCapabilitiesEvent`, capability lookups silently return empty optionals at runtime even if the field appears populated.

**How to apply:**
```java
// In mod constructor:
modEventBus.addListener(this::registerCapabilities);

private void registerCapabilities(RegisterCapabilitiesEvent event) {
    event.register(BondData.class);  // your capability data class
}
```

The `CapabilityManager.get(new CapabilityToken<>(){})` field initializer is still used as a lazy reference holder — that part is fine. The key change is ALSO calling `event.register()`.

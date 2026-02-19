package io.github.kosianodangoo.trialmonolith.transformer.agent;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.FileSystemException;
import java.security.ProtectionDomain;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import io.github.kosianodangoo.trialmonolith.transformer.GenericTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class TheTrialMonolithAgent implements ClassFileTransformer {
    private static boolean loaded = false;
    public static Instrumentation inst = null;

    public static void premain(String args, Instrumentation inst) {
        initAgent(inst);
    }

    public static void agentmain(String args, Instrumentation inst) {
        initAgent(inst);
    }

    private static void initAgent(Instrumentation inst) {
        TheTrialMonolithAgent.inst = inst;
        inst.addTransformer(new TheTrialMonolithAgent(), true);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className.startsWith("io/github/kosianodangoo/trialmonolith/transformer")) {
            return null;
        }
        try {
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassNode classNode = new ClassNode();
            cr.accept(classNode, 0);

            boolean modified = GenericTransformer.transform(GenericTransformer.Phase.ClassFileTransformer, classNode);
            if (modified) {
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES) {
                    @Override
                    protected String getCommonSuperClass(String type1, String type2) {
                        try {
                            return super.getCommonSuperClass(type1, type2);
                        } catch (Exception e) {
                            return "java/lang/Object";
                        }
                    }
                };
                classNode.accept(cw);
                return cw.toByteArray();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        if (inst != null) {
            return;
        }
        try {
            File agentJar = createAgentJar();
            attachAgent(agentJar);
        }
        catch (Exception ignored) {
        }
    }

    private static File createAgentJar() throws Exception {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "trial-monolith-agent");
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            throw new FileSystemException(tempDir.getName(), null, "Failed to make directory");
        }
        File agentJar = new File(tempDir, "trial-monolith-agent.jar");
        Manifest manifest = new Manifest();
        Attributes attrs = manifest.getMainAttributes();
        attrs.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attrs.putValue("Agent-Class", "io.github.kosianodangoo.trialmonolith.transformer.agent.ClassFileTransformer");
        attrs.putValue("Can-Retransform-Classes", "true");
        attrs.putValue("Can-Redefine-Classes", "true");
        JarOutputStream jos = new JarOutputStream(new FileOutputStream(agentJar), manifest);
        jos.close();
        return agentJar;
    }

    private static void attachAgent(File agentJar) throws Exception {
        String pid = getCurrentPid();
        enableSelfAttach();
        Class<?> vmClass = findVirtualMachineClass();
        if (vmClass == null) {
            throw new ClassNotFoundException("VirtualMachine not available - tools.jar or jdk.attach module missing");
        }
        Method attachMethod = vmClass.getMethod("attach", String.class);
        Object vm = attachMethod.invoke(null, pid);
        try {
            Method loadAgentMethod = vmClass.getMethod("loadAgent", String.class);
            loadAgentMethod.invoke(vm, agentJar.getAbsolutePath());
        }
        finally {
            Method detachMethod = vmClass.getMethod("detach");
            detachMethod.invoke(vm);
        }
    }

    private static void enableSelfAttach() {
        try {
            Class<?> hotSpotVMClass = Class.forName("sun.tools.attach.HotSpotVirtualMachine");
            Field allowAttachSelfField = hotSpotVMClass.getDeclaredField("ALLOW_ATTACH_SELF");
            Object unsafe = getUnsafe();
            if (unsafe == null) {
                return;
            }
            Class<?> unsafeClass = unsafe.getClass();
            Method staticFieldBase = unsafeClass.getMethod("staticFieldBase", Field.class);
            Method staticFieldOffset = unsafeClass.getMethod("staticFieldOffset", Field.class);
            Method putBoolean = unsafeClass.getMethod("putBoolean", Object.class, Long.TYPE, Boolean.TYPE);
            Object base = staticFieldBase.invoke(unsafe, allowAttachSelfField);
            long offset = (Long)staticFieldOffset.invoke(unsafe, allowAttachSelfField);
            putBoolean.invoke(unsafe, base, offset, true);
        }
        catch (Exception ignored) {
        }
    }

    private static Object getUnsafe() {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return field.get(null);
        }
        catch (Exception e) {
            return null;
        }
    }

    private static Class<?> findVirtualMachineClass() {
        for (String name : new String[]{"com.sun.tools.attach.VirtualMachine", "jdk.attach.VirtualMachine"}) {
            try {
                return Class.forName(name);
            }
            catch (ClassNotFoundException ignored) {
            }
        }
        try {
            return ClassLoader.getSystemClassLoader().loadClass("com.sun.tools.attach.VirtualMachine");
        }
        catch (ClassNotFoundException classNotFoundException) {
            return null;
        }
    }

    private static String getCurrentPid() {
        String runtimeName = ManagementFactory.getRuntimeMXBean().getName();
        return runtimeName.split("@")[0];
    }
}
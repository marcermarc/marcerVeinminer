package de.marcermarc.lock.objects;

import org.bukkit.Material;

public class MaterialType {
    private Material m_Material;
    private int m_Type;
    private boolean m_TypeSet;

    public MaterialType(String in_Material, int in_Type) {
        m_Material = Material.getMaterial(in_Material);
        m_Type = in_Type;
        m_TypeSet = true;
    }

    public MaterialType(Material in_Material, int in_Type) {
        m_Material = in_Material;
        m_Type = in_Type;
        m_TypeSet = true;
    }

    public MaterialType(Material in_Material) {
        m_Material = in_Material;
        m_TypeSet = false;
    }

    public MaterialType(String in_Material) {
        String[] s = in_Material.split(":");

        if (s.length == 2) {
            m_Material = Material.getMaterial(s[0]);
            m_Type = Integer.parseInt(s[1]);
            m_TypeSet = true;
        } else if (s.length == 1) {
            m_Material = Material.getMaterial(s[0]);
            m_TypeSet = false;
        }
    }

    @Override
    public String toString() {
        if (m_TypeSet) {
            return m_Material.name() + ":" + m_Type;
        } else {
            return m_Material.name();
        }
    }

    @Override
    public boolean equals(Object obj) {
        MaterialType m = (MaterialType) obj;
        return !m_TypeSet || !m.m_TypeSet ? m_Material.equals(m.m_Material) : m_Material.equals(m.m_Material) && m_Type == m.m_Type;
    }

    @Override
    public int hashCode() {
        return m_Material.name().hashCode();
    }

    // region Getter und Setter

    public Material getMaterial() {
        return m_Material;
    }

    public int getType() {
        return m_Type;
    }

    // endregion

}

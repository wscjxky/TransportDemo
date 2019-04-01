<template>
  <q-item>
    <q-item-side :icon="icon || $utils.getFileIcon(file.name.split('.').pop())" :color="color"></q-item-side>
    <q-item-main>
      <q-item-tile label class="overflow-hidden">{{file.name}} </q-item-tile>
      <q-item-tile sublabel>{{file.size | readable('Size')}}&nbsp;&nbsp;<span v-if="!noprogress">{{progress}}%</span></q-item-tile>
      <q-progress :percentage="progress" :animate="true" :color="color" v-if="!noprogress" />

    </q-item-main>
    <q-item-side v-for="icon in after" :key="icon.icon" v-show="!uploading" :icon="icon.icon" right :color="icon.color" @click.native="click(icon.click)" class="click"></q-item-side>
  </q-item>
</template>

<script>
import * as qiniu from 'qiniu-js'
export default {
  mounted() {
    let observer = {
      next: (res) => {
        this.uploading = true
        this.file.__uploaded = res.total.loaded
        this.file.__progress = res.total.percent
        this.progress = res.total.percent
      },
      error: (err) => {
        this.file.__failed = true
        this.uploading = false
        this.file.__doneUploading = true
        console.log(err)
        this.$q.notify({message: this.file.name + this.$t('messages.uploadFailed'), type: 'negative', position: 'top'})
      },
      complete: (res) => {
        this.file.__doneUploading = true
        this.doneUploading = true
        this.$emit('doneUploading', this.index)
        this.$http.post('/api/attachments/', res)
          .then((res) => {
            this.file.__data = res.data
            this.$emit('uploaded', res.data)
            this.$emit('remove', this.index)
          })
      }
    }
    this.file.__observer = observer
    var putExtra = {
      fname: this.file.name,
      params: {
        'x:publisher': this.$store.state.user.user.id
      },
      mimeType: null
    }
    var config = {
      useCdnDomain: false,
      region: qiniu.region.z1
    }
    if (this.getUpToken) {
      this.$http.get('/api/attachments/uptoken/')
        .then((resp) => {
          this.uptoken = resp.data.uptoken
          var observable = qiniu.upload(this.file, this.$utils.getFileKey(this.file.name), resp.data.uptoken, putExtra, config)
          this.file.__observable = observable
        })
    }
  },
  props: {
    file: {
      required: true
    },
    index: {
      type: Number,
      required: true
    },
    after: {
      type: Array,
      default: () => []
    },
    color: {
      type: String,
      default: 'primary'
    },
    noprogress: {
      type: Boolean,
      default: false
    },
    icon: {
      type: String,
      default: null
    },
    getUpToken: {
      type: Boolean,
      default: true,
      required: false
    }
  },
  data() {
    return {
      uptoken: '',
      progress: 0,
      doneUploading: false,
      uploading: false
    }
  },
  methods: {
    click(func) {
      func.call(this)
    }
  }
}
</script>

<style lang="stylus" scoped>
.click {
  cursor: pointer;
}
</style>